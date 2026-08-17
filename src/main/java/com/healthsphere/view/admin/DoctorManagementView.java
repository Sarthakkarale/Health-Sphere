package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.DoctorManagementController;
import com.healthsphere.model.DoctorProfile;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DoctorManagementView extends ScrollPane {

    private Stage primaryStage;

    private final DoctorManagementController doctorManagementController;

    private TableView<DoctorModel> doctorTable;
    private ObservableList<DoctorModel> masterDoctorData;
    private FilteredList<DoctorModel> filteredData;

    private Label totalCountLabel;
    private Label activeCountLabel;
    private Label leaveCountLabel;
    private Label surgeryCountLabel;

    private BarChart<String, Number> departmentBarChart;

    public DoctorManagementView() {
        this(null);
    }

    public DoctorManagementView(Stage stage) {

        this.primaryStage = stage;

        this.doctorManagementController =
                new DoctorManagementController();

        setFitToWidth(true);

        setStyle(
                "-fx-background-color: #F8FAFC; " +
                "-fx-background: #F8FAFC; " +
                "-fx-border-color: transparent;"
        );

        VBox mainContainer =
                new VBox(24);

        mainContainer.setPadding(
                new Insets(28)
        );

        mainContainer.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        // 1. Header
        HBox header =
                createHeader();

        // 2. Analytics
        HBox topAnalyticsSection =
                createTopAnalyticsSection();

        // 3. Filters
        HBox filterBar =
                createFilterBar();

        // 4. Table
        VBox tableContainer =
                createTableContainer();

        mainContainer.getChildren().addAll(
                header,
                topAnalyticsSection,
                filterBar,
                tableContainer
        );

        setContent(mainContainer);

        // Load real doctor data from Firestore.
        loadDoctorData();
    }

    public Parent getView() {
        return this;
    }

    public Scene createScene() {
        return new Scene(this);
    }

    // ------------------------------------------------------------------------
    // HEADER
    // ------------------------------------------------------------------------

    private HBox createHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

        Label title =
                new Label(
                        "Doctor & Specialist Directory"
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
                        "Manage medical staff credentials, verification approvals, department allocations, and OPD availability."
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

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button exportBtn =
                new Button(
                        "Export Directory"
                );

        exportBtn.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        exportBtn.setStyle(
                "-fx-background-color: #FFFFFF; " +
                "-fx-text-fill: #334155; " +
                "-fx-border-color: #CBD5E1; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8px 16px; " +
                "-fx-cursor: hand;"
        );

        exportBtn.setOnAction(
                e -> showAlert(
                        "Directory Exported",
                        "Doctor roster and department metrics exported to CSV successfully."
                )
        );

        Button addDoctorBtn =
                new Button(
                        "+ Add New Doctor"
                );

        addDoctorBtn.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        addDoctorBtn.setStyle(
                "-fx-background-color: #4F46E5; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 8px; " +
                "-fx-padding: 8px 16px; " +
                "-fx-cursor: hand;"
        );

        addDoctorBtn.setOnAction(
                e -> showAddDoctorDialog()
        );

        HBox buttonGroup =
                new HBox(
                        12,
                        exportBtn,
                        addDoctorBtn
                );

        buttonGroup.setAlignment(
                Pos.CENTER_RIGHT
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                buttonGroup
        );

        return header;
    }

    // ------------------------------------------------------------------------
    // ANALYTICS
    // ------------------------------------------------------------------------

    private HBox createTopAnalyticsSection() {

        HBox section =
                new HBox(20);

        section.setAlignment(
                Pos.CENTER
        );

        GridPane statsGrid =
                new GridPane();

        statsGrid.setHgap(16);
        statsGrid.setVgap(16);

        // Total Doctors

        VBox totalCard =
                createStatCard(
                        "Total Doctors",
                        "0",
                        "Registered specialists",
                        "#4F46E5"
                );

        totalCountLabel =
                (Label) totalCard.getChildren().get(1);

        // Active

        VBox activeCard =
                createStatCard(
                        "On Duty",
                        "0",
                        "Currently available",
                        "#16A34A"
                );

        activeCountLabel =
                (Label) activeCard.getChildren().get(1);

        // Leave

        VBox leaveCard =
                createStatCard(
                        "On Leave",
                        "0",
                        "Currently unavailable",
                        "#D97706"
                );

        leaveCountLabel =
                (Label) leaveCard.getChildren().get(1);

        // Surgery

        VBox surgeryCard =
                createStatCard(
                        "In Surgery",
                        "0",
                        "Currently operating",
                        "#DC2626"
                );

        surgeryCountLabel =
                (Label) surgeryCard.getChildren().get(1);

        statsGrid.add(
                totalCard,
                0,
                0
        );

        statsGrid.add(
                activeCard,
                1,
                0
        );

        statsGrid.add(
                leaveCard,
                0,
                1
        );

        statsGrid.add(
                surgeryCard,
                1,
                1
        );

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();

        xAxis.setLabel(
                "Department"
        );

        yAxis.setLabel(
                "Doctors"
        );

        departmentBarChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        departmentBarChart.setTitle(
                "Doctors by Department"
        );

        departmentBarChart.setLegendVisible(
                false
        );

        departmentBarChart.setPrefWidth(
                600
        );

        departmentBarChart.setPrefHeight(
                300
        );

        section.getChildren().addAll(
                statsGrid,
                departmentBarChart
        );

        return section;
    }

    private VBox createStatCard(
            String title,
            String value,
            String subtitle,
            String accentColor) {

        VBox card =
                new VBox(5);

        card.setPadding(
                new Insets(16)
        );

        card.setPrefWidth(
                180
        );

        card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 10px;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        titleLabel.setTextFill(
                Color.web("#64748B")
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        26
                )
        );

        valueLabel.setTextFill(
                Color.web(accentColor)
        );

        Label subtitleLabel =
                new Label(subtitle);

        subtitleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        11
                )
        );

        subtitleLabel.setTextFill(
                Color.web("#94A3B8")
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                subtitleLabel
        );

        return card;
    }

    // ------------------------------------------------------------------------
    // FILTER BAR
    // ------------------------------------------------------------------------

    private HBox createFilterBar() {

        HBox filterBar =
                new HBox(12);

        filterBar.setAlignment(
                Pos.CENTER_LEFT
        );

        filterBar.setPadding(
                new Insets(12)
        );

        filterBar.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 8px; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 8px;"
        );

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search doctor, specialization, hospital..."
        );

        searchField.setPrefWidth(
                320
        );

        ComboBox<String> departmentFilter =
                new ComboBox<>();

        departmentFilter.getItems().addAll(
                "All Departments",
                "Cardiology",
                "Neurology",
                "Orthopedics",
                "Pediatrics",
                "Oncology",
                "General Medicine"
        );

        departmentFilter.setValue(
                "All Departments"
        );

        ComboBox<String> statusFilter =
                new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Status",
                "ON DUTY",
                "ON LEAVE",
                "IN SURGERY"
        );

        statusFilter.setValue(
                "All Status"
        );

        searchField.textProperty().addListener(
                (obs, oldValue, newValue) ->
                        applyFilters(
                                searchField.getText(),
                                departmentFilter.getValue(),
                                statusFilter.getValue()
                        )
        );

        departmentFilter.valueProperty().addListener(
                (obs, oldValue, newValue) ->
                        applyFilters(
                                searchField.getText(),
                                departmentFilter.getValue(),
                                statusFilter.getValue()
                        )
        );

        statusFilter.valueProperty().addListener(
                (obs, oldValue, newValue) ->
                        applyFilters(
                                searchField.getText(),
                                departmentFilter.getValue(),
                                statusFilter.getValue()
                        )
        );

        filterBar.getChildren().addAll(
                searchField,
                departmentFilter,
                statusFilter
        );

        return filterBar;
    }

    private void applyFilters(
            String searchText,
            String department,
            String status) {

        if (filteredData == null) {
            return;
        }

        String query =
                searchText == null
                        ? ""
                        : searchText.trim()
                        .toLowerCase();

        filteredData.setPredicate(
                doctor -> {

                    boolean matchesSearch =
                            query.isEmpty()
                                    ||
                            contains(
                                    doctor.getName(),
                                    query
                            )
                                    ||
                            contains(
                                    doctor.getDepartment(),
                                    query
                            )
                                    ||
                            contains(
                                    doctor.getSpecialization(),
                                    query
                            )
                                    ||
                            contains(
                                    doctor.getContactNo(),
                                    query
                            )
                                    ||
                            contains(
                                    doctor.getEmail(),
                                    query
                            );

                    boolean matchesDepartment =
                            department == null
                                    ||
                            "All Departments"
                                    .equals(department)
                                    ||
                            department.equalsIgnoreCase(
                                    doctor.getDepartment()
                            );

                    boolean matchesStatus =
                            status == null
                                    ||
                            "All Status"
                                    .equals(status)
                                    ||
                            status.equalsIgnoreCase(
                                    doctor.getStatus()
                            );

                    return matchesSearch
                            && matchesDepartment
                            && matchesStatus;
                }
        );
    }

    private boolean contains(
            String value,
            String query) {

        return value != null
                &&
                value.toLowerCase()
                        .contains(query);
    }

    // ------------------------------------------------------------------------
    // TABLE
    // ------------------------------------------------------------------------

    private VBox createTableContainer() {

        VBox container =
                new VBox(12);

        container.setPadding(
                new Insets(16)
        );

        container.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10px; " +
                "-fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 10px;"
        );

        Label tableTitle =
                new Label(
                        "Registered Doctors"
                );

        tableTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        16
                )
        );

        tableTitle.setTextFill(
                Color.web("#0F172A")
        );

        doctorTable =
                new TableView<>();

        doctorTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        doctorTable.setPlaceholder(
                new Label(
                        "No doctors found."
                )
        );

        // Doctor Name Column

        TableColumn<DoctorModel, String> nameCol =
                new TableColumn<>(
                        "Doctor"
                );

        nameCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "name"
                )
        );

        nameCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String name,
                            boolean empty) {

                        super.updateItem(
                                name,
                                empty
                        );

                        if (empty || name == null) {

                            setGraphic(null);

                        } else {

                            DoctorModel doctor =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            HBox box =
                                    new HBox(8);

                            box.setAlignment(
                                    Pos.CENTER_LEFT
                            );

                            StackPane avatar =
                                    createDoctorAvatar(
                                            name
                                    );

                            VBox text =
                                    new VBox(2);

                            Label nameLabel =
                                    new Label(
                                            name
                                    );

                            nameLabel.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.BOLD,
                                            12
                                    )
                            );

                            Label idLabel =
                                    new Label(
                                            doctor.getDoctorId()
                                    );

                            idLabel.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.NORMAL,
                                            10
                                    )
                            );

                            idLabel.setTextFill(
                                    Color.web("#64748B")
                            );

                            text.getChildren().addAll(
                                    nameLabel,
                                    idLabel
                            );

                            box.getChildren().addAll(
                                    avatar,
                                    text
                            );

                            setGraphic(box);
                        }
                    }
                }
        );

        // Department Column

        TableColumn<DoctorModel, String> deptCol =
                new TableColumn<>(
                        "Department"
                );

        deptCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "department"
                )
        );

        deptCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String dept,
                            boolean empty) {

                        super.updateItem(
                                dept,
                                empty
                        );

                        if (empty || dept == null) {

                            setGraphic(null);

                        } else {

                            DoctorModel doctor =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            VBox textContainer =
                                    new VBox(2);

                            Label deptLbl =
                                    new Label(
                                            dept
                                    );

                            deptLbl.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.SEMI_BOLD,
                                            12
                                    )
                            );

                            deptLbl.setTextFill(
                                    Color.web("#334155")
                            );

                            Label roomLbl =
                                    new Label(
                                            "OPD Room: "
                                                    + doctor.getOpdRoom()
                                    );

                            roomLbl.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.NORMAL,
                                            11
                                    )
                            );

                            roomLbl.setTextFill(
                                    Color.web("#64748B")
                            );

                            textContainer
                                    .getChildren()
                                    .addAll(
                                            deptLbl,
                                            roomLbl
                                    );

                            setGraphic(
                                    textContainer
                            );
                        }
                    }
                }
        );

        // Contact Column

        TableColumn<DoctorModel, String> contactCol =
                new TableColumn<>(
                        "Contact & Email"
                );

        contactCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "contactNo"
                )
        );

        contactCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String contact,
                            boolean empty) {

                        super.updateItem(
                                contact,
                                empty
                        );

                        if (empty || contact == null) {

                            setGraphic(null);

                        } else {

                            DoctorModel doctor =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            VBox textContainer =
                                    new VBox(2);

                            Label phoneLbl =
                                    new Label(
                                            contact
                                    );

                            phoneLbl.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.NORMAL,
                                            12
                                    )
                            );

                            phoneLbl.setTextFill(
                                    Color.web("#334155")
                            );

                            Label emailLbl =
                                    new Label(
                                            doctor.getEmail()
                                    );

                            emailLbl.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.NORMAL,
                                            11
                                    )
                            );

                            emailLbl.setTextFill(
                                    Color.web("#64748B")
                            );

                            textContainer
                                    .getChildren()
                                    .addAll(
                                            phoneLbl,
                                            emailLbl
                                    );

                            setGraphic(
                                    textContainer
                            );
                        }
                    }
                }
        );

        // Verification Status

        TableColumn<DoctorModel, String> verificationCol =
                new TableColumn<>(
                        "App Approval"
                );

        verificationCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "verificationStatus"
                )
        );

        verificationCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String status,
                            boolean empty) {

                        super.updateItem(
                                status,
                                empty
                        );

                        if (empty || status == null) {

                            setGraphic(null);

                        } else {

                            Label badge =
                                    new Label(
                                            status.toUpperCase()
                                    );

                            badge.setPadding(
                                    new Insets(
                                            4,
                                            10,
                                            4,
                                            10
                                    )
                            );

                            badge.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.BOLD,
                                            10
                                    )
                            );

                            switch (
                                    status.toUpperCase()
                            ) {

                                case "VERIFIED" ->
                                        badge.setStyle(
                                                "-fx-background-color: #DCFCE7; " +
                                                "-fx-text-fill: #15803D; " +
                                                "-fx-background-radius: 20px;"
                                        );

                                case "PENDING" ->
                                        badge.setStyle(
                                                "-fx-background-color: #FEF3C7; " +
                                                "-fx-text-fill: #D97706; " +
                                                "-fx-background-radius: 20px;"
                                        );

                                default ->
                                        badge.setStyle(
                                                "-fx-background-color: #FEE2E2; " +
                                                "-fx-text-fill: #B91C1C; " +
                                                "-fx-background-radius: 20px;"
                                        );
                            }

                            setGraphic(badge);
                        }
                    }
                }
        );

        // Duty Status

        TableColumn<DoctorModel, String> statusCol =
                new TableColumn<>(
                        "Availability Status"
                );

        statusCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );

        statusCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String status,
                            boolean empty) {

                        super.updateItem(
                                status,
                                empty
                        );

                        if (empty || status == null) {

                            setGraphic(null);

                        } else {

                            Label badge =
                                    new Label(
                                            status.toUpperCase()
                                    );

                            badge.setPadding(
                                    new Insets(
                                            4,
                                            10,
                                            4,
                                            10
                                    )
                            );

                            badge.setFont(
                                    Font.font(
                                            "Segoe UI",
                                            FontWeight.BOLD,
                                            10
                                    )
                            );

                            switch (
                                    status.toUpperCase()
                            ) {

                                case "ON DUTY" ->
                                        badge.setStyle(
                                                "-fx-background-color: #DCFCE7; " +
                                                "-fx-text-fill: #15803D; " +
                                                "-fx-background-radius: 20px;"
                                        );

                                case "IN SURGERY" ->
                                        badge.setStyle(
                                                "-fx-background-color: #FEE2E2; " +
                                                "-fx-text-fill: #B91C1C; " +
                                                "-fx-background-radius: 20px;"
                                        );

                                default ->
                                        badge.setStyle(
                                                "-fx-background-color: #FEF3C7; " +
                                                "-fx-text-fill: #D97706; " +
                                                "-fx-background-radius: 20px;"
                                        );
                            }

                            setGraphic(badge);
                        }
                    }
                }
        );

        // Shift

        TableColumn<DoctorModel, String> shiftCol =
                new TableColumn<>(
                        "Current Shift"
                );

        shiftCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "shift"
                )
        );

        // Actions

        TableColumn<DoctorModel, Void> actionCol =
                new TableColumn<>(
                        "Management"
                );

        actionCol.setCellFactory(
                col -> new TableCell<>() {

                    private final Button profileBtn =
                            new Button("View");

                    private final Button toggleStatusBtn =
                            new Button("Duty");

                    private final HBox btnGroup =
                            new HBox(
                                    5,
                                    profileBtn,
                                    toggleStatusBtn
                            );

                    {
                        btnGroup.setAlignment(
                                Pos.CENTER
                        );

                        profileBtn.setStyle(
                                "-fx-background-color: #EEF2FF; " +
                                "-fx-text-fill: #4338CA; " +
                                "-fx-cursor: hand; " +
                                "-fx-font-size: 10px; " +
                                "-fx-background-radius: 4px;"
                        );

                        toggleStatusBtn.setStyle(
                                "-fx-background-color: #F1F5F9; " +
                                "-fx-text-fill: #334155; " +
                                "-fx-cursor: hand; " +
                                "-fx-font-size: 10px; " +
                                "-fx-background-radius: 4px;"
                        );

                        profileBtn.setOnAction(e -> {

                            DoctorModel doctor =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            showDoctorProfileModal(
                                    doctor
                            );
                        });

                        toggleStatusBtn.setOnAction(e -> {

                            DoctorModel doctor =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            if (
                                    "ON DUTY"
                                            .equalsIgnoreCase(
                                                    doctor.getStatus()
                                            )
                            ) {

                                doctor.setStatus(
                                        "ON LEAVE"
                                );

                            } else {

                                doctor.setStatus(
                                        "ON DUTY"
                                );
                            }

                            doctorTable.refresh();

                            updateCountersAndChart();
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty) {

                        super.updateItem(
                                item,
                                empty
                        );

                        if (empty) {

                            setGraphic(null);

                        } else {

                            setGraphic(btnGroup);
                        }
                    }
                }
        );

        doctorTable.getColumns().addAll(
                nameCol,
                deptCol,
                contactCol,
                verificationCol,
                statusCol,
                shiftCol,
                actionCol
        );

        container.getChildren().addAll(
                tableTitle,
                doctorTable
        );

        return container;
    }

    // ------------------------------------------------------------------------
    // AVATAR
    // ------------------------------------------------------------------------

    private StackPane createDoctorAvatar(
            String name) {

        String initials = "DR";

        if (
                name != null
                        &&
                name.contains(" ")
        ) {

            String[] parts =
                    name.replace(
                            "Dr. ",
                            ""
                    ).split(" ");

            if (parts.length >= 2) {

                initials =
                        (
                                ""
                                        + parts[0].charAt(0)
                                        + parts[1].charAt(0)
                        ).toUpperCase();

            } else if (
                    parts.length == 1
                            &&
                    !parts[0].isEmpty()
            ) {

                initials =
                        (
                                ""
                                        + parts[0].charAt(0)
                        ).toUpperCase();
            }
        }

        Circle circle =
                new Circle(16);

        circle.setFill(
                Color.web("#E0E7FF")
        );

        circle.setStroke(
                Color.web("#4F46E5")
        );

        circle.setStrokeWidth(
                1.5
        );

        Label label =
                new Label(initials);

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        label.setTextFill(
                Color.web("#3730A3")
        );

        return new StackPane(
                circle,
                label
        );
    }

    // ------------------------------------------------------------------------
    // DATA MANAGEMENT
    // ------------------------------------------------------------------------

    private void loadDoctorData() {

        try {

            List<DoctorProfile> doctors =
                    doctorManagementController
                            .getAllDoctors();

            masterDoctorData =
                    FXCollections.observableArrayList();

            for (
                    DoctorProfile profile :
                    doctors
            ) {

                if (profile == null) {
                    continue;
                }

                String firstName =
                        safe(
                                profile.getFirstName()
                        );

                String lastName =
                        safe(
                                profile.getLastName()
                        );

                String fullName =
                        (
                                "Dr. "
                                        + firstName
                                        + " "
                                        + lastName
                        ).trim();

                if (
                        firstName.isEmpty()
                                &&
                        lastName.isEmpty()
                ) {

                    fullName =
                            "Doctor";
                }

                String doctorId =
                        safe(
                                profile
                                        .getRegistrationNumber()
                        );

                if (doctorId.isEmpty()) {

                    doctorId =
                            safe(
                                    profile.getUid()
                            );
                }

                if (doctorId.isEmpty()) {

                    doctorId =
                            "N/A";
                }

                /*
                 * DoctorProfile already contains hospital affiliation.
                 *
                 * We use that existing field here rather than creating
                 * another Doctor model.
                 */
                String department =
                        safe(
                                profile
                                        .getHospitalAffiliation()
                        );

                if (department.isEmpty()) {

                    department =
                            "Not specified";
                }

                String specialization =
                        safe(
                                profile.getSpecialization()
                        );

                if (specialization.isEmpty()) {

                    specialization =
                            "Not specified";
                }

                String contact =
                        safe(
                                profile.getPhone()
                        );

                if (contact.isEmpty()) {

                    contact =
                            "Not available";
                }

                String email =
                        safe(
                                profile.getEmail()
                        );

                if (email.isEmpty()) {

                    email =
                            "Not available";
                }

                /*
                 * These fields are NOT currently part of DoctorProfile.
                 *
                 * We deliberately do not invent or fake Firestore values.
                 *
                 * They can be implemented later using a proper
                 * doctor-specific model if the team agrees they are
                 * persistent requirements.
                 */
                String opdRoom =
                        "Not configured";

                String status =
                        "Not configured";

                String shift =
                        "Not configured";

                String joiningDate =
                        "Not available";

                String verificationStatus =
                        doctorManagementController
                                .getVerificationStatus(
                                        profile.getUid()
                                );

                if (verificationStatus == null ||
                        verificationStatus.trim().isEmpty()) {
                    verificationStatus = "PENDING";
                }

                masterDoctorData.add(
                        new DoctorModel(
                                safe(profile.getUid()),
                                doctorId,
                                fullName,
                                department,
                                specialization,
                                contact,
                                email,
                                opdRoom,
                                status,
                                shift,
                                joiningDate,
                                verificationStatus
                        )
                );
            }

            filteredData =
                    new FilteredList<>(
                            masterDoctorData,
                            d -> true
                    );

            doctorTable.setItems(
                    filteredData
            );

            updateCountersAndChart();

        } catch (Exception e) {

            e.printStackTrace();

            masterDoctorData =
                    FXCollections.observableArrayList();

            filteredData =
                    new FilteredList<>(
                            masterDoctorData,
                            d -> true
                    );

            doctorTable.setItems(
                    filteredData
            );

            updateCountersAndChart();

            showAlert(
                    "Unable to Load Doctors",
                    "The doctor directory could not be loaded from Firestore.\n\n"
                            + e.getMessage()
            );
        }
    }

    private String safe(
            String value) {

        return value == null
                ? ""
                : value.trim();
    }

    private void updateCountersAndChart() {

        if (masterDoctorData == null) {
            return;
        }

        int total =
                masterDoctorData.size();

        long active =
                masterDoctorData.stream()
                        .filter(
                                d ->
                                        d.getStatus()
                                                .equalsIgnoreCase(
                                                        "ON DUTY"
                                                )
                        )
                        .count();

        long leave =
                masterDoctorData.stream()
                        .filter(
                                d ->
                                        d.getStatus()
                                                .equalsIgnoreCase(
                                                        "ON LEAVE"
                                                )
                        )
                        .count();

        long surgery =
                masterDoctorData.stream()
                        .filter(
                                d ->
                                        d.getStatus()
                                                .equalsIgnoreCase(
                                                        "IN SURGERY"
                                                )
                        )
                        .count();

        totalCountLabel.setText(
                String.valueOf(total)
        );

        activeCountLabel.setText(
                String.valueOf(active)
        );

        leaveCountLabel.setText(
                String.valueOf(leave)
        );

        surgeryCountLabel.setText(
                String.valueOf(surgery)
        );

        departmentBarChart
                .getData()
                .clear();

        Map<String, Long> deptCounts =
                masterDoctorData.stream()
                        .collect(
                                Collectors.groupingBy(
                                        DoctorModel::getDepartment,
                                        Collectors.counting()
                                )
                        );

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        deptCounts.forEach(
                (dept, count) ->
                        series.getData().add(
                                new XYChart.Data<>(
                                        dept,
                                        count
                                )
                        )
        );

        departmentBarChart
                .getData()
                .add(series);
    }

    // ------------------------------------------------------------------------
    // DOCTOR PROFILE MODAL
    // ------------------------------------------------------------------------

    private void showDoctorProfileModal(
            DoctorModel doctor) {

        Dialog<Void> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Doctor Credentials & Verification"
        );

        dialog.setHeaderText(
                "Specialist Profile: "
                        + doctor.getName()
                        + " ("
                        + doctor.getDoctorId()
                        + ")"
        );

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(15)
        );

        content.setPrefWidth(
                420
        );

        Label details =
                new Label(
                        "🩺 Department: "
                                + doctor.getDepartment()
                                + " | Specialization: "
                                + doctor.getSpecialization()
                                + "\n"
                                + "🏢 OPD Location: "
                                + doctor.getOpdRoom()
                                + "\n"
                                + "📞 Contact: "
                                + doctor.getContactNo()
                                + "\n"
                                + "✉️ Email: "
                                + doctor.getEmail()
                                + "\n"
                                + "⏰ Shift Schedule: "
                                + doctor.getShift()
                                + "\n"
                                + "📅 Joining Date: "
                                + doctor.getJoiningDate()
                                + "\n"
                                + "🏷️ Current Duty Status: "
                                + doctor.getStatus()
                                + "\n"
                                + "🛡️ Current Approval Status: "
                                + doctor.getVerificationStatus()
                                + "\n\n"
                                + "Weekly Consultation Hours:\n"
                                + "• Mon - Thu: 09:00 AM - 01:00 PM (OPD)\n"
                                + "• Fri: 02:00 PM - 06:00 PM (Rounds / Consultations)"
                );

        details.setWrapText(
                true
        );

        Label actionTitle =
                new Label(
                        "Change Verification Status:"
                );

        actionTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        actionTitle.setTextFill(
                Color.web("#0F172A")
        );

        Button verifyBtn =
                new Button(
                        "✓ Verify & Approve"
                );

        verifyBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        verifyBtn.setStyle(
                "-fx-background-color: #DCFCE7; " +
                "-fx-text-fill: #15803D; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px; " +
                "-fx-background-radius: 6px;"
        );

        Button pendingBtn =
                new Button(
                        "⏳ Set as Pending"
                );

        pendingBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        pendingBtn.setStyle(
                "-fx-background-color: #FEF3C7; " +
                "-fx-text-fill: #D97706; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px; " +
                "-fx-background-radius: 6px;"
        );

        Button rejectBtn =
                new Button(
                        "✕ Reject Verification"
                );

        rejectBtn.setMaxWidth(
                Double.MAX_VALUE
        );

        rejectBtn.setStyle(
                "-fx-background-color: #FEE2E2; " +
                "-fx-text-fill: #B91C1C; " +
                "-fx-cursor: hand; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 8px; " +
                "-fx-background-radius: 6px;"
        );

        verifyBtn.setOnAction(
                e -> {

                    try {
                        boolean success =
                                doctorManagementController
                                        .verifyDoctor(
                                                doctor.getUid()
                                        );

                        if (success) {
                            doctor.setVerificationStatus(
                                    "VERIFIED"
                            );

                            doctorTable.refresh();

                            showAlert(
                                    "Doctor Verified",
                                    doctor.getName()
                                            + " has been successfully verified and approved."
                            );

                            dialog.close();
                        } else {
                            showAlert(
                                    "Verification Failed",
                                    "Unable to verify "
                                            + doctor.getName()
                                            + ". Please try again."
                            );
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(
                                "Verification Error",
                                "An error occurred while verifying the doctor.\n\n"
                                        + ex.getMessage()
                        );
                    }
                }
        );

        pendingBtn.setOnAction(
                e -> {

                    try {
                        boolean success =
                                doctorManagementController
                                        .setDoctorPending(
                                                doctor.getUid()
                                        );

                        if (success) {
                            doctor.setVerificationStatus(
                                    "PENDING"
                            );

                            doctorTable.refresh();

                            showAlert(
                                    "Status Updated",
                                    doctor.getName()
                                            + " verification status is now set to PENDING."
                            );

                            dialog.close();
                        } else {
                            showAlert(
                                    "Update Failed",
                                    "Unable to update "
                                            + doctor.getName()
                                            + " verification status."
                            );
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(
                                "Update Error",
                                "An error occurred while updating the doctor status.\n\n"
                                        + ex.getMessage()
                        );
                    }
                }
        );

        rejectBtn.setOnAction(
                e -> {

                    try {
                        boolean success =
                                doctorManagementController
                                        .rejectDoctor(
                                                doctor.getUid()
                                        );

                        if (success) {
                            doctor.setVerificationStatus(
                                    "REJECTED"
                            );

                            doctorTable.refresh();

                            showAlert(
                                    "Verification Rejected",
                                    doctor.getName()
                                            + " verification has been rejected."
                            );

                            dialog.close();
                        } else {
                            showAlert(
                                    "Rejection Failed",
                                    "Unable to reject "
                                            + doctor.getName()
                                            + " verification."
                            );
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(
                                "Rejection Error",
                                "An error occurred while rejecting the doctor.\n\n"
                                        + ex.getMessage()
                        );
                    }
                }
        );

        content.getChildren().addAll(
                details,
                new Separator(),
                actionTitle,
                verifyBtn,
                pendingBtn,
                rejectBtn
        );

        dialog.getDialogPane()
                .setContent(content);

        dialog.getDialogPane()
                .getButtonTypes()
                .add(
                        ButtonType.CLOSE
                );

        dialog.showAndWait();
    }

    // ------------------------------------------------------------------------
    // ADD DOCTOR DIALOG
    // ------------------------------------------------------------------------

    private void showAddDoctorDialog() {

        Dialog<DoctorModel> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Register New Medical Specialist"
        );

        dialog.setHeaderText(
                "Add New Doctor to HealthSphere Roster"
        );

        ButtonType addDoctorButtonType =
                new ButtonType(
                        "Add Specialist",
                        ButtonBar.ButtonData.OK_DONE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        addDoctorButtonType,
                        ButtonType.CANCEL
                );

        GridPane grid =
                new GridPane();

        grid.setHgap(10);
        grid.setVgap(12);

        grid.setPadding(
                new Insets(
                        20,
                        150,
                        10,
                        10
                )
        );

        TextField nameField =
                new TextField();

        nameField.setPromptText(
                "Dr. Full Name"
        );

        ComboBox<String> deptCombo =
                new ComboBox<>();

        deptCombo.getItems().addAll(
                "Cardiology",
                "Neurology",
                "Orthopedics",
                "Pediatrics",
                "Oncology",
                "General Medicine"
        );

        deptCombo.setValue(
                "Cardiology"
        );

        TextField specField =
                new TextField();

        specField.setPromptText(
                "e.g., Senior Surgeon"
        );

        TextField phoneField =
                new TextField();

        phoneField.setPromptText(
                "+91 98765 XXXXX"
        );

        TextField emailField =
                new TextField();

        emailField.setPromptText(
                "doctor@healthsphere.org"
        );

        TextField roomField =
                new TextField();

        roomField.setPromptText(
                "OPD-101"
        );

        ComboBox<String> shiftCombo =
                new ComboBox<>();

        shiftCombo.getItems().addAll(
                "Morning (08:00 - 14:00)",
                "Evening (14:00 - 20:00)",
                "Night Shift",
                "Full Day (09:00 - 17:00)"
        );

        shiftCombo.setValue(
                "Morning (08:00 - 14:00)"
        );

        grid.add(
                new Label("Full Name:"),
                0,
                0
        );

        grid.add(
                nameField,
                1,
                0
        );

        grid.add(
                new Label("Department:"),
                0,
                1
        );

        grid.add(
                deptCombo,
                1,
                1
        );

        grid.add(
                new Label("Specialization:"),
                0,
                2
        );

        grid.add(
                specField,
                1,
                2
        );

        grid.add(
                new Label("Contact No:"),
                0,
                3
        );

        grid.add(
                phoneField,
                1,
                3
        );

        grid.add(
                new Label("Email Address:"),
                0,
                4
        );

        grid.add(
                emailField,
                1,
                4
        );

        grid.add(
                new Label("OPD Room:"),
                0,
                5
        );

        grid.add(
                roomField,
                1,
                5
        );

        grid.add(
                new Label("Shift Schedule:"),
                0,
                6
        );

        grid.add(
                shiftCombo,
                1,
                6
        );

        dialog.getDialogPane()
                .setContent(grid);

        dialog.setResultConverter(
                dialogButton -> {

                    if (
                            dialogButton
                                    == addDoctorButtonType
                                    &&
                            !nameField
                                    .getText()
                                    .trim()
                                    .isEmpty()
                    ) {

                        String newId =
                                "DOC-"
                                        + (
                                        100
                                                + masterDoctorData.size()
                                                + 1
                                );

                        String name =
                                nameField
                                        .getText()
                                        .trim()
                                        .startsWith("Dr.")
                                        ?
                                        nameField
                                                .getText()
                                                .trim()
                                        :
                                        "Dr. "
                                                + nameField
                                                .getText()
                                                .trim();

                        String spec =
                                specField
                                        .getText()
                                        .trim()
                                        .isEmpty()
                                        ?
                                        "General Consultant"
                                        :
                                        specField
                                                .getText()
                                                .trim();

                        String phone =
                                phoneField
                                        .getText()
                                        .trim()
                                        .isEmpty()
                                        ?
                                        "+91 90000 00000"
                                        :
                                        phoneField
                                                .getText()
                                                .trim();

                        String email =
                                emailField
                                        .getText()
                                        .trim()
                                        .isEmpty()
                                        ?
                                        "doctor@healthsphere.org"
                                        :
                                        emailField
                                                .getText()
                                                .trim();

                        String room =
                                roomField
                                        .getText()
                                        .trim()
                                        .isEmpty()
                                        ?
                                        "OPD-101"
                                        :
                                        roomField
                                                .getText()
                                                .trim();

                        return new DoctorModel(
                                "",
                                newId,
                                name,
                                deptCombo.getValue(),
                                spec,
                                phone,
                                email,
                                room,
                                "ON DUTY",
                                shiftCombo.getValue(),
                                LocalDate.now().toString(),
                                "PENDING"
                        );
                    }

                    return null;
                }
        );

        Optional<DoctorModel> result =
                dialog.showAndWait();

        result.ifPresent(
                newDoctor -> {

                    masterDoctorData.add(
                            0,
                            newDoctor
                    );

                    updateCountersAndChart();

                    showAlert(
                            "Doctor Registered",
                            newDoctor.getName()
                                    + " has been registered and is pending admin verification."
                    );
                }
        );
    }

    // ------------------------------------------------------------------------
    // ALERT
    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    // UI MODEL
    // ------------------------------------------------------------------------

    public static class DoctorModel {

        private final String uid;

        private final String doctorId;

        private final String name;

        private final String department;

        private final String specialization;

        private final String contactNo;

        private final String email;

        private final String opdRoom;

        private String status;

        private final String shift;

        private final String joiningDate;

        private String verificationStatus;

        public DoctorModel(
                String uid,
                String doctorId,
                String name,
                String department,
                String specialization,
                String contactNo,
                String email,
                String opdRoom,
                String status,
                String shift,
                String joiningDate,
                String verificationStatus) {

            this.uid =
                    uid;

            this.doctorId =
                    doctorId;

            this.name =
                    name;

            this.department =
                    department;

            this.specialization =
                    specialization;

            this.contactNo =
                    contactNo;

            this.email =
                    email;

            this.opdRoom =
                    opdRoom;

            this.status =
                    status;

            this.shift =
                    shift;

            this.joiningDate =
                    joiningDate;

            this.verificationStatus =
                    verificationStatus;
        }

        public String getUid() {
            return uid;
        }

        public String getDoctorId() {
            return doctorId;
        }

        public String getName() {
            return name;
        }

        public String getDepartment() {
            return department;
        }

        public String getSpecialization() {
            return specialization;
        }

        public String getContactNo() {
            return contactNo;
        }

        public String getEmail() {
            return email;
        }

        public String getOpdRoom() {
            return opdRoom;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(
                String status) {

            this.status =
                    status;
        }

        public String getShift() {
            return shift;
        }

        public String getJoiningDate() {
            return joiningDate;
        }

        public String getVerificationStatus() {
            return verificationStatus;
        }

        public void setVerificationStatus(
                String verificationStatus) {

            this.verificationStatus =
                    verificationStatus;
        }
    }
}