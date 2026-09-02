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

import java.util.Map;
import java.util.stream.Collectors;

public class DoctorManagementView extends ScrollPane {

    private Stage primaryStage;

    private TableView<DoctorModel> doctorTable;

    private ObservableList<DoctorModel> masterDoctorData;
    private FilteredList<DoctorModel> filteredData;

    private Label totalCountLabel;
    private Label activeCountLabel;
    private Label pendingCountLabel;
    private Label rejectedCountLabel;

    private BarChart<String, Number> specializationBarChart;

    private final DoctorManagementController doctorController;

    public DoctorManagementView() {
        this(null);
    }

    public DoctorManagementView(Stage stage) {

        this.primaryStage = stage;
        this.doctorController = new DoctorManagementController();

        setFitToWidth(true);
        setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );

        VBox mainContainer = new VBox(24);
        mainContainer.setPadding(new Insets(28));
        mainContainer.setStyle("-fx-background-color: #F8FAFC;");

        HBox header = createHeader();

        HBox analytics = createAnalyticsSection();

        HBox filterBar = createFilterBar();

        VBox tableContainer = createTableContainer();

        mainContainer.getChildren().addAll(
                header,
                analytics,
                filterBar,
                tableContainer
        );

        setContent(mainContainer);

        loadDoctorData();
    }

    public Parent getView() {
        return this;
    }

    public Scene createScene() {
        return new Scene(this);
    }

    // ============================================================
    // HEADER
    // ============================================================

    private HBox createHeader() {

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);

        Label title = new Label(
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

        Label subtitle = new Label(
                "Manage doctor profiles, credentials and verification approvals."
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

        Region spacer = new Region();
        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button refreshBtn =
                new Button("↻ Refresh Doctors");

        refreshBtn.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        refreshBtn.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-text-fill: #334155;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-padding: 8px 16px;" +
                "-fx-cursor: hand;"
        );

        refreshBtn.setOnAction(
                e -> loadDoctorData()
        );

        header.getChildren().addAll(
                titleBox,
                spacer,
                refreshBtn
        );

        return header;
    }

    // ============================================================
    // ANALYTICS
    // ============================================================

    private HBox createAnalyticsSection() {

        HBox section = new HBox(20);
        section.setAlignment(Pos.CENTER);

        GridPane statsGrid = new GridPane();

        statsGrid.setHgap(16);
        statsGrid.setVgap(16);

        HBox.setHgrow(
                statsGrid,
                Priority.ALWAYS
        );

        totalCountLabel = new Label("0");
        activeCountLabel = new Label("0");
        pendingCountLabel = new Label("0");
        rejectedCountLabel = new Label("0");

        VBox totalCard =
                createStatCard(
                        "Total Doctors",
                        totalCountLabel,
                        "Registered Doctors",
                        "#4F46E5"
                );

        VBox activeCard =
                createStatCard(
                        "Approved",
                        activeCountLabel,
                        "Verified Doctors",
                        "#059669"
                );

        VBox pendingCard =
                createStatCard(
                        "Pending",
                        pendingCountLabel,
                        "Awaiting Verification",
                        "#D97706"
                );

        VBox rejectedCard =
                createStatCard(
                        "Rejected",
                        rejectedCountLabel,
                        "Verification Rejected",
                        "#DC2626"
                );

        ColumnConstraints c1 =
                new ColumnConstraints();

        c1.setPercentWidth(50);

        ColumnConstraints c2 =
                new ColumnConstraints();

        c2.setPercentWidth(50);

        statsGrid
                .getColumnConstraints()
                .addAll(c1, c2);

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
                pendingCard,
                0,
                1
        );

        statsGrid.add(
                rejectedCard,
                1,
                1
        );

        VBox chartCard = new VBox(12);

        chartCard.setPadding(
                new Insets(16)
        );

        chartCard.setMinWidth(420);

        chartCard.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label chartTitle =
                new Label(
                        "Specialization Breakdown"
                );

        chartTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        14
                )
        );

        chartTitle.setTextFill(
                Color.web("#0F172A")
        );

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();

        xAxis.setTickLabelFill(
                Color.web("#64748B")
        );

        yAxis.setTickLabelFill(
                Color.web("#64748B")
        );

        specializationBarChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        specializationBarChart.setPrefHeight(180);
        specializationBarChart.setLegendVisible(false);
        specializationBarChart.setAnimated(false);

        chartCard.getChildren().addAll(
                chartTitle,
                specializationBarChart
        );

        section.getChildren().addAll(
                statsGrid,
                chartCard
        );

        return section;
    }

    private VBox createStatCard(
            String title,
            Label valueLabel,
            String subtext,
            String accentColorHex) {

        VBox card = new VBox(6);

        card.setPadding(
                new Insets(16)
        );

        card.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label titleLabel =
                new Label(title);

        titleLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.SEMI_BOLD,
                        12
                )
        );

        titleLabel.setTextFill(
                Color.web("#64748B")
        );

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        22
                )
        );

        valueLabel.setTextFill(
                Color.web("#0F172A")
        );

        Label subLabel =
                new Label(subtext);

        subLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        11
                )
        );

        subLabel.setTextFill(
                Color.web(accentColorHex)
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                subLabel
        );

        return card;
    }

    // ============================================================
    // FILTER BAR
    // ============================================================

    private HBox createFilterBar() {

        HBox bar = new HBox(14);

        bar.setAlignment(
                Pos.CENTER_LEFT
        );

        bar.setPadding(
                new Insets(14)
        );

        bar.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 10px;"
        );

        TextField searchInput =
                new TextField();

        searchInput.setPromptText(
                "🔍 Search Doctor, UID, Specialization or Email..."
        );

        searchInput.setPrefWidth(300);

        searchInput.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 6px;" +
                "-fx-padding: 8px 12px;"
        );

        ComboBox<String> verificationFilter =
                new ComboBox<>();

        verificationFilter.getItems().addAll(
                "All Approvals",
                "APPROVED",
                "PENDING",
                "REJECTED"
        );

        verificationFilter.setValue(
                "All Approvals"
        );

        verificationFilter.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 6px;"
        );

        ComboBox<String> specializationFilter =
                new ComboBox<>();

        specializationFilter.getItems().add(
                "All Specializations"
        );

        specializationFilter.setValue(
                "All Specializations"
        );

        specializationFilter.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-text-fill: #0F172A;" +
                "-fx-border-color: #CBD5E1;" +
                "-fx-border-radius: 6px;"
        );

        Runnable applyFilter = () -> {

            String query =
                    searchInput
                            .getText()
                            .toLowerCase()
                            .trim();

            String selectedVerification =
                    verificationFilter.getValue();

            String selectedSpecialization =
                    specializationFilter.getValue();

            filteredData.setPredicate(
                    doctor -> {

                        boolean matchesQuery =
                                query.isEmpty()
                                        ||
                                doctor.getName()
                                        .toLowerCase()
                                        .contains(query)
                                        ||
                                doctor.getDoctorId()
                                        .toLowerCase()
                                        .contains(query)
                                        ||
                                doctor.getSpecialization()
                                        .toLowerCase()
                                        .contains(query)
                                        ||
                                doctor.getEmail()
                                        .toLowerCase()
                                        .contains(query);

                        boolean matchesVerification =
                                selectedVerification.equals(
                                        "All Approvals"
                                )
                                ||
                                doctor
                                        .getVerificationStatus()
                                        .equalsIgnoreCase(
                                                selectedVerification
                                        );

                        boolean matchesSpecialization =
                                selectedSpecialization.equals(
                                        "All Specializations"
                                )
                                ||
                                doctor
                                        .getSpecialization()
                                        .equalsIgnoreCase(
                                                selectedSpecialization
                                        );

                        return matchesQuery
                                && matchesVerification
                                && matchesSpecialization;
                    }
            );
        };

        searchInput.textProperty()
                .addListener(
                        (obs, oldVal, newVal)
                                -> applyFilter.run()
                );

        verificationFilter.valueProperty()
                .addListener(
                        (obs, oldVal, newVal)
                                -> applyFilter.run()
                );

        specializationFilter.valueProperty()
                .addListener(
                        (obs, oldVal, newVal)
                                -> applyFilter.run()
                );

        Region spacer = new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Button resetBtn =
                new Button("Reset Filters");

        resetBtn.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        11
                )
        );

        resetBtn.setStyle(
                "-fx-background-color: #F1F5F9;" +
                "-fx-text-fill: #475569;" +
                "-fx-background-radius: 6px;" +
                "-fx-padding: 8px 14px;" +
                "-fx-cursor: hand;"
        );

        resetBtn.setOnAction(
                e -> {

                    searchInput.clear();

                    verificationFilter.setValue(
                            "All Approvals"
                    );

                    specializationFilter.setValue(
                            "All Specializations"
                    );
                }
        );

        bar.getChildren().addAll(
                searchInput,
                verificationFilter,
                specializationFilter,
                spacer,
                resetBtn
        );

        return bar;
    }

    // ============================================================
    // TABLE
    // ============================================================

    private VBox createTableContainer() {

        VBox container = new VBox(12);

        container.setPadding(
                new Insets(16)
        );

        container.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 12px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 12px;"
        );

        Label tableTitle =
                new Label(
                        "Medical Staff & Specialist Roster"
                );

        tableTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        15
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

        doctorTable.setStyle(
                "-fx-background-color: transparent;"
        );

        doctorTable.setPrefHeight(500);

        // --------------------------------------------------------
        // DOCTOR
        // --------------------------------------------------------

        TableColumn<DoctorModel, String> nameCol =
                new TableColumn<>(
                        "Doctor Profile"
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
                            return;
                        }

                        DoctorModel doctor =
                                getTableView()
                                        .getItems()
                                        .get(getIndex());

                        HBox box =
                                new HBox(12);

                        box.setAlignment(
                                Pos.CENTER_LEFT
                        );

                        StackPane icon =
                                createDoctorAvatar(
                                        name
                                );

                        VBox text =
                                new VBox(2);

                        Label nameLbl =
                                new Label(name);

                        nameLbl.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.BOLD,
                                        13
                                )
                        );

                        nameLbl.setTextFill(
                                Color.web("#0F172A")
                        );

                        Label idLbl =
                                new Label(
                                        "UID: "
                                                + doctor.getDoctorId()
                                );

                        idLbl.setFont(
                                Font.font(
                                        "Segoe UI",
                                        10
                                )
                        );

                        idLbl.setTextFill(
                                Color.web("#64748B")
                        );

                        text.getChildren().addAll(
                                nameLbl,
                                idLbl
                        );

                        box.getChildren().addAll(
                                icon,
                                text
                        );

                        setGraphic(box);
                    }
                }
        );

        // --------------------------------------------------------
        // SPECIALIZATION
        // --------------------------------------------------------

        TableColumn<DoctorModel, String> specializationCol =
                new TableColumn<>(
                        "Specialization"
                );

        specializationCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "specialization"
                )
        );

        specializationCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String specialization,
                            boolean empty) {

                        super.updateItem(
                                specialization,
                                empty
                        );

                        if (empty ||
                                specialization == null) {

                            setGraphic(null);
                            return;
                        }

                        DoctorModel doctor =
                                getTableView()
                                        .getItems()
                                        .get(getIndex());

                        VBox box =
                                new VBox(3);

                        Label spec =
                                new Label(
                                        specialization
                                );

                        spec.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.SEMI_BOLD,
                                        12
                                )
                        );

                        spec.setTextFill(
                                Color.web("#334155")
                        );

                        Label experience =
                                new Label(
                                        "Experience: "
                                                + doctor.getExperience()
                                                + " years"
                                );

                        experience.setFont(
                                Font.font(
                                        "Segoe UI",
                                        10
                                )
                        );

                        experience.setTextFill(
                                Color.web("#64748B")
                        );

                        box.getChildren().addAll(
                                spec,
                                experience
                        );

                        setGraphic(box);
                    }
                }
        );

        // --------------------------------------------------------
        // HOSPITAL
        // --------------------------------------------------------

        TableColumn<DoctorModel, String> hospitalCol =
                new TableColumn<>(
                        "Hospital Affiliation"
                );

        hospitalCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "hospitalAffiliation"
                )
        );

        hospitalCol.setCellFactory(
                col -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            String hospital,
                            boolean empty) {

                        super.updateItem(
                                hospital,
                                empty
                        );

                        if (empty ||
                                hospital == null) {

                            setGraphic(null);
                            return;
                        }

                        VBox box =
                                new VBox(3);

                        Label hospitalLabel =
                                new Label(hospital);

                        hospitalLabel.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.SEMI_BOLD,
                                        12
                                )
                        );

                        hospitalLabel.setTextFill(
                                Color.web("#334155")
                        );

                        DoctorModel doctor =
                                getTableView()
                                        .getItems()
                                        .get(getIndex());

                        Label council =
                                new Label(
                                        "Council: "
                                                + doctor.getMedicalCouncil()
                                );

                        council.setFont(
                                Font.font(
                                        "Segoe UI",
                                        10
                                )
                        );

                        council.setTextFill(
                                Color.web("#64748B")
                        );

                        box.getChildren().addAll(
                                hospitalLabel,
                                council
                        );

                        setGraphic(box);
                    }
                }
        );

        // --------------------------------------------------------
        // CONTACT
        // --------------------------------------------------------

        TableColumn<DoctorModel, String> contactCol =
                new TableColumn<>(
                        "Contact"
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
                            String phone,
                            boolean empty) {

                        super.updateItem(
                                phone,
                                empty
                        );

                        if (empty ||
                                phone == null) {

                            setGraphic(null);
                            return;
                        }

                        DoctorModel doctor =
                                getTableView()
                                        .getItems()
                                        .get(getIndex());

                        VBox box =
                                new VBox(3);

                        Label phoneLabel =
                                new Label(phone);

                        phoneLabel.setFont(
                                Font.font(
                                        "Segoe UI",
                                        12
                                )
                        );

                        phoneLabel.setTextFill(
                                Color.web("#334155")
                        );

                        Label emailLabel =
                                new Label(
                                        doctor.getEmail()
                                );

                        emailLabel.setFont(
                                Font.font(
                                        "Segoe UI",
                                        10
                                )
                        );

                        emailLabel.setTextFill(
                                Color.web("#64748B")
                        );

                        box.getChildren().addAll(
                                phoneLabel,
                                emailLabel
                        );

                        setGraphic(box);
                    }
                }
        );

        // --------------------------------------------------------
        // REGISTRATION NUMBER
        // --------------------------------------------------------

        TableColumn<DoctorModel, String> registrationCol =
                new TableColumn<>(
                        "Registration No."
                );

        registrationCol.setCellValueFactory(
                new PropertyValueFactory<>(
                        "registrationNumber"
                )
        );

        // --------------------------------------------------------
        // VERIFICATION
        // --------------------------------------------------------

        TableColumn<DoctorModel, String> verificationCol =
                new TableColumn<>(
                        "Approval Status"
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
                            return;
                        }

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

                            case "APPROVED" ->
                                    badge.setStyle(
                                            "-fx-background-color: #DCFCE7;" +
                                            "-fx-text-fill: #15803D;" +
                                            "-fx-background-radius: 20px;"
                                    );

                            case "PENDING" ->
                                    badge.setStyle(
                                            "-fx-background-color: #FEF3C7;" +
                                            "-fx-text-fill: #D97706;" +
                                            "-fx-background-radius: 20px;"
                                    );

                            default ->
                                    badge.setStyle(
                                            "-fx-background-color: #FEE2E2;" +
                                            "-fx-text-fill: #B91C1C;" +
                                            "-fx-background-radius: 20px;"
                                    );
                        }

                        setGraphic(badge);
                    }
                }
        );

        // --------------------------------------------------------
        // ACTIONS
        // --------------------------------------------------------

        TableColumn<DoctorModel, Void> actionCol =
                new TableColumn<>(
                        "Management"
                );

        actionCol.setCellFactory(
                col -> new TableCell<>() {

                    private final Button viewBtn =
                            new Button("View");

                    private final Button verifyBtn =
                            new Button("Verify");

                    private final HBox buttons =
                            new HBox(
                                    5,
                                    viewBtn,
                                    verifyBtn
                            );

                    {

                        buttons.setAlignment(
                                Pos.CENTER
                        );

                        viewBtn.setStyle(
                                "-fx-background-color: #EEF2FF;" +
                                "-fx-text-fill: #4338CA;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 10px;" +
                                "-fx-background-radius: 4px;"
                        );

                        verifyBtn.setStyle(
                                "-fx-background-color: #DCFCE7;" +
                                "-fx-text-fill: #15803D;" +
                                "-fx-cursor: hand;" +
                                "-fx-font-size: 10px;" +
                                "-fx-background-radius: 4px;"
                        );

                        viewBtn.setOnAction(
                                e -> {

                                    if (getIndex() < 0 ||
                                            getIndex() >=
                                                    getTableView()
                                                            .getItems()
                                                            .size()) {
                                        return;
                                    }

                                    DoctorModel doctor =
                                            getTableView()
                                                    .getItems()
                                                    .get(getIndex());

                                    showDoctorProfileModal(
                                            doctor
                                    );
                                }
                        );

                        verifyBtn.setOnAction(
                                e -> {

                                    if (getIndex() < 0 ||
                                            getIndex() >=
                                                    getTableView()
                                                            .getItems()
                                                            .size()) {
                                        return;
                                    }

                                    DoctorModel doctor =
                                            getTableView()
                                                    .getItems()
                                                    .get(getIndex());

                                    approveDoctor(
                                            doctor
                                    );
                                }
                        );
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

                            DoctorModel doctor =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            if (
                                    "APPROVED"
                                            .equalsIgnoreCase(
                                                    doctor.getVerificationStatus()
                                            )
                            ) {

                                verifyBtn.setText(
                                        "Approved"
                                );

                                verifyBtn.setDisable(
                                        true
                                );

                            } else {

                                verifyBtn.setText(
                                        "Verify"
                                );

                                verifyBtn.setDisable(
                                        false
                                );
                            }

                            setGraphic(buttons);
                        }
                    }
                }
        );

        doctorTable.getColumns().addAll(
                nameCol,
                specializationCol,
                hospitalCol,
                contactCol,
                registrationCol,
                verificationCol,
                actionCol
        );

        container.getChildren().addAll(
                tableTitle,
                doctorTable
        );

        return container;
    }

    // ============================================================
    // LOAD REAL FIRESTORE DOCTORS
    // ============================================================

    private void loadDoctorData() {

        try {

            ListWrapper doctors =
                    new ListWrapper(
                            doctorController.getAllDoctors()
                    );

            masterDoctorData =
                    FXCollections.observableArrayList();

            for (
                    DoctorProfile profile :
                    doctors.getDoctors()
            ) {

                if (profile == null) {
                    continue;
                }

                String status =
                        doctorController
                                .getVerificationStatus(
                                        profile.getUid()
                                );

                if (status == null ||
                        status.trim().isEmpty()) {

                    status = "PENDING";
                }

                masterDoctorData.add(
                        convertToDoctorModel(
                                profile,
                                status
                        )
                );
            }

            filteredData =
                    new FilteredList<>(
                            masterDoctorData,
                            doctor -> true
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
                            doctor -> true
                    );

            doctorTable.setItems(
                    filteredData
            );

            updateCountersAndChart();

            showAlert(
                    "Unable to Load Doctors",
                    "Could not load doctor profiles from Firestore.\n\n"
                            + e.getMessage()
            );
        }
    }

    private DoctorModel convertToDoctorModel(
            DoctorProfile profile,
            String verificationStatus) {

        String firstName =
                safe(profile.getFirstName());

        String lastName =
                safe(profile.getLastName());

        String name =
                ("Dr. "
                        + firstName
                        + " "
                        + lastName)
                        .trim();

        if (name.equals("Dr.")) {
            name = "Doctor";
        }

        return new DoctorModel(
                safe(profile.getUid()),
                name,
                safe(profile.getSpecialization()),
                safe(profile.getPhone()),
                safe(profile.getEmail()),
                safe(profile.getRegistrationNumber()),
                safe(profile.getExperience()),
                safe(profile.getHospitalAffiliation()),
                safe(profile.getMedicalCouncil()),
                verificationStatus
        );
    }

    // ============================================================
    // ANALYTICS UPDATE
    // ============================================================

    private void updateCountersAndChart() {

        if (masterDoctorData == null) {
            return;
        }

        int total =
                masterDoctorData.size();

        long approved =
                masterDoctorData
                        .stream()
                        .filter(
                                d -> "APPROVED"
                                        .equalsIgnoreCase(
                                                d.getVerificationStatus()
                                        )
                        )
                        .count();

        long pending =
                masterDoctorData
                        .stream()
                        .filter(
                                d -> "PENDING"
                                        .equalsIgnoreCase(
                                                d.getVerificationStatus()
                                        )
                        )
                        .count();

        long rejected =
                masterDoctorData
                        .stream()
                        .filter(
                                d -> "REJECTED"
                                        .equalsIgnoreCase(
                                                d.getVerificationStatus()
                                        )
                        )
                        .count();

        totalCountLabel.setText(
                String.valueOf(total)
        );

        activeCountLabel.setText(
                String.valueOf(approved)
        );

        pendingCountLabel.setText(
                String.valueOf(pending)
        );

        rejectedCountLabel.setText(
                String.valueOf(rejected)
        );

        specializationBarChart
                .getData()
                .clear();

        Map<String, Long> counts =
                masterDoctorData
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        d -> {

                                            String spec =
                                                    d.getSpecialization();

                                            return spec.isEmpty()
                                                    ? "Not Specified"
                                                    : spec;
                                        },
                                        Collectors.counting()
                                )
                        );

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        counts.forEach(
                (specialization, count) ->
                        series.getData().add(
                                new XYChart.Data<>(
                                        specialization,
                                        count
                                )
                        )
        );

        specializationBarChart
                .getData()
                .add(series);
    }

    // ============================================================
    // VIEW PROFILE
    // ============================================================

    private void showDoctorProfileModal(
            DoctorModel doctor) {

        Dialog<Void> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Doctor Credentials & Verification"
        );

        dialog.setHeaderText(
                doctor.getName()
                        + "\nUID: "
                        + doctor.getDoctorId()
        );

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(15)
        );

        content.setPrefWidth(480);

        Label details =
                new Label(
                        "🩺 Specialization: "
                                + doctor.getSpecialization()
                                + "\n\n"
                                + "🏢 Hospital Affiliation: "
                                + doctor.getHospitalAffiliation()
                                + "\n\n"
                                + "📞 Phone: "
                                + doctor.getContactNo()
                                + "\n\n"
                                + "✉️ Email: "
                                + doctor.getEmail()
                                + "\n\n"
                                + "🏷️ Registration Number: "
                                + doctor.getRegistrationNumber()
                                + "\n\n"
                                + "🎓 Experience: "
                                + doctor.getExperience()
                                + "\n\n"
                                + "🏛️ Medical Council: "
                                + doctor.getMedicalCouncil()
                                + "\n\n"
                                + "🛡️ Verification Status: "
                                + doctor.getVerificationStatus()
                );

        details.setWrapText(true);

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
                "-fx-background-color: #DCFCE7;" +
                "-fx-text-fill: #15803D;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px;" +
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
                "-fx-background-color: #FEF3C7;" +
                "-fx-text-fill: #D97706;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px;" +
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
                "-fx-background-color: #FEE2E2;" +
                "-fx-text-fill: #B91C1C;" +
                "-fx-cursor: hand;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 8px;" +
                "-fx-background-radius: 6px;"
        );

        verifyBtn.setOnAction(
                e -> {

                    if (
                            doctorController.verifyDoctor(
                                    doctor.getDoctorId()
                            )
                    ) {

                        doctor.setVerificationStatus(
                                "APPROVED"
                        );

                        doctorTable.refresh();

                        updateCountersAndChart();

                        showAlert(
                                "Doctor Approved",
                                doctor.getName()
                                        + " has been successfully approved."
                        );

                        dialog.close();

                    } else {

                        showAlert(
                                "Update Failed",
                                "Unable to approve "
                                        + doctor.getName()
                        );
                    }
                }
        );

        pendingBtn.setOnAction(
                e -> {

                    if (
                            doctorController.setDoctorPending(
                                    doctor.getDoctorId()
                            )
                    ) {

                        doctor.setVerificationStatus(
                                "PENDING"
                        );

                        doctorTable.refresh();

                        updateCountersAndChart();

                        showAlert(
                                "Status Updated",
                                doctor.getName()
                                        + " is now PENDING."
                        );

                        dialog.close();

                    } else {

                        showAlert(
                                "Update Failed",
                                "Unable to change verification status."
                        );
                    }
                }
        );

        rejectBtn.setOnAction(
                e -> {

                    if (
                            doctorController.rejectDoctor(
                                    doctor.getDoctorId()
                            )
                    ) {

                        doctor.setVerificationStatus(
                                "REJECTED"
                        );

                        doctorTable.refresh();

                        updateCountersAndChart();

                        showAlert(
                                "Verification Rejected",
                                doctor.getName()
                                        + " has been rejected."
                        );

                        dialog.close();

                    } else {

                        showAlert(
                                "Update Failed",
                                "Unable to reject "
                                        + doctor.getName()
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

    // ============================================================
    // QUICK APPROVE
    // ============================================================

    private void approveDoctor(
            DoctorModel doctor) {

        if (
                "APPROVED"
                        .equalsIgnoreCase(
                                doctor.getVerificationStatus()
                        )
        ) {
            return;
        }

        boolean success =
                doctorController.verifyDoctor(
                        doctor.getDoctorId()
                );

        if (success) {

            doctor.setVerificationStatus(
                    "APPROVED"
            );

            doctorTable.refresh();

            updateCountersAndChart();

            showAlert(
                    "Doctor Approved",
                    doctor.getName()
                            + " has been successfully approved."
            );

        } else {

            showAlert(
                    "Approval Failed",
                    "Unable to approve "
                            + doctor.getName()
            );
        }
    }

    // ============================================================
    // AVATAR
    // ============================================================

    private StackPane createDoctorAvatar(
            String name) {

        String initials = "DR";

        if (name != null &&
                !name.trim().isEmpty()) {

            String cleaned =
                    name.replace(
                            "Dr.",
                            ""
                    ).trim();

            String[] parts =
                    cleaned.split("\\s+");

            if (parts.length >= 2) {

                initials =
                        (
                                ""
                                        + parts[0].charAt(0)
                                        + parts[1].charAt(0)
                        ).toUpperCase();

            } else {

                initials =
                        String.valueOf(
                                cleaned.charAt(0)
                        ).toUpperCase();
            }
        }

        Circle circle =
                new Circle(17);

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

    // ============================================================
    // HELPERS
    // ============================================================

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }

    private void showAlert(
            String title,
            String content) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // ============================================================
    // SMALL WRAPPER
    // ============================================================

    private static class ListWrapper {

        private final java.util.List<DoctorProfile> doctors;

        ListWrapper(
                java.util.List<DoctorProfile> doctors) {

            this.doctors =
                    doctors == null
                            ? java.util.List.of()
                            : doctors;
        }

        java.util.List<DoctorProfile> getDoctors() {
            return doctors;
        }
    }

    // ============================================================
    // TABLE MODEL
    // ============================================================

    public static class DoctorModel {

        private final String doctorId;
        private final String name;
        private final String specialization;
        private final String contactNo;
        private final String email;
        private final String registrationNumber;
        private final String experience;
        private final String hospitalAffiliation;
        private final String medicalCouncil;

        private String verificationStatus;

        public DoctorModel(
                String doctorId,
                String name,
                String specialization,
                String contactNo,
                String email,
                String registrationNumber,
                String experience,
                String hospitalAffiliation,
                String medicalCouncil,
                String verificationStatus) {

            this.doctorId =
                    doctorId;

            this.name =
                    name;

            this.specialization =
                    specialization;

            this.contactNo =
                    contactNo;

            this.email =
                    email;

            this.registrationNumber =
                    registrationNumber;

            this.experience =
                    experience;

            this.hospitalAffiliation =
                    hospitalAffiliation;

            this.medicalCouncil =
                    medicalCouncil;

            this.verificationStatus =
                    verificationStatus;
        }

        public String getDoctorId() {
            return doctorId;
        }

        public String getName() {
            return name;
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

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public String getExperience() {
            return experience;
        }

        public String getHospitalAffiliation() {
            return hospitalAffiliation;
        }

        public String getMedicalCouncil() {
            return medicalCouncil;
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