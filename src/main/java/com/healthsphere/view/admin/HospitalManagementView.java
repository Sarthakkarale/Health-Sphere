package com.healthsphere.view.admin;

import com.healthsphere.controller.admin.HospitalVerificationController;
import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.model.HospitalProfile;
import com.healthsphere.model.HospitalVerification;

import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HospitalManagementView extends ScrollPane {

    private Stage primaryStage;

    private TableView<HospitalProfile> hospitalTable;

    private ObservableList<HospitalProfile> masterHospitalData;

    private FilteredList<HospitalProfile> filteredData;

    private Label totalCountLabel;
    private Label pendingCountLabel;
    private Label verifiedCountLabel;
    private Label rejectedCountLabel;

    private BarChart<String, Number> cityBarChart;

    private ComboBox<String> cityFilter;

    private final HospitalDAO hospitalDAO;

    private final HospitalVerificationController verificationController;

    /*
     * Firestore-loaded verification data.
     *
     * Firestore remains the source of truth.
     */
    private final Map<String, HospitalVerification> verificationMap =
            new HashMap<>();

    /*
     * Temporary admin UID.
     *
     * Replace this later with your SessionManager /
     * Firebase Authentication logged-in admin UID.
     */
    private static final String ADMIN_UID = "ADMIN";


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public HospitalManagementView() {
        this(null);
    }

    public HospitalManagementView(Stage stage) {

        this.primaryStage = stage;

        this.hospitalDAO = new HospitalDAO();

        this.verificationController =
                new HospitalVerificationController();

        setFitToWidth(true);

        setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background: #F8FAFC;" +
                "-fx-border-color: transparent;"
        );

        VBox mainContainer = new VBox(24);

        mainContainer.setPadding(
                new Insets(28)
        );

        mainContainer.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        HBox header = createHeader();

        HBox topAnalyticsSection =
                createTopAnalyticsSection();

        HBox filterBar =
                createFilterBar();

        VBox tableContainer =
                createTableContainer();

        mainContainer.getChildren().addAll(
                header,
                topAnalyticsSection,
                filterBar,
                tableContainer
        );

        setContent(mainContainer);

        loadHospitalData();
    }


    // ============================================================
    // VIEW / SCENE
    // ============================================================

    /*
     * Returns this view as a Parent.
     */
    public Parent getView() {
        return this;
    }

    /*
     * IMPORTANT:
     *
     * Do NOT create getScene() here.
     *
     * ScrollPane extends Node and JavaFX already provides
     * a final getScene() method.
     *
     * Use createScene() when you need a new Scene.
     */
    public Scene createScene() {
        return new Scene(this);
    }


    // ============================================================
    // HEADER
    // ============================================================

    private HBox createHeader() {

        HBox header = new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox = new VBox(4);

        Label title =
                new Label(
                        "Hospital Verification & Regulatory Oversight"
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
                        "Inspect healthcare facility credentials, compliance certifications, bed capacity, and authorization requests."
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

        header.getChildren().addAll(
                titleBox,
                spacer
        );

        return header;
    }


    // ============================================================
    // ANALYTICS
    // ============================================================

    private HBox createTopAnalyticsSection() {

        HBox section = new HBox(20);

        section.setAlignment(
                Pos.CENTER
        );

        GridPane statsGrid = new GridPane();

        statsGrid.setHgap(16);
        statsGrid.setVgap(16);

        statsGrid.setPrefWidth(620);
        statsGrid.setMinWidth(520);

        HBox.setHgrow(
                statsGrid,
                Priority.ALWAYS
        );

        totalCountLabel =
                new Label("0");

        pendingCountLabel =
                new Label("0");

        verifiedCountLabel =
                new Label("0");

        rejectedCountLabel =
                new Label("0");

        VBox totalCard =
                createStatCard(
                        "Total Facilities",
                        totalCountLabel,
                        "Registered on Platform",
                        "#4F46E5",
                        "#EEF2FF"
                );

        VBox pendingCard =
                createStatCard(
                        "Pending Verifications",
                        pendingCountLabel,
                        "Action Required",
                        "#D97706",
                        "#FFFBEB"
                );

        VBox verifiedCard =
                createStatCard(
                        "Verified Facilities",
                        verifiedCountLabel,
                        "Compliance Clear",
                        "#059669",
                        "#ECFDF5"
                );

        VBox rejectedCard =
                createStatCard(
                        "Rejected Applications",
                        rejectedCountLabel,
                        "Document Audit Failed",
                        "#DC2626",
                        "#FEF2F2"
                );

        ColumnConstraints c1 =
                new ColumnConstraints();

        c1.setPercentWidth(50);

        ColumnConstraints c2 =
                new ColumnConstraints();

        c2.setPercentWidth(50);

        statsGrid.getColumnConstraints().addAll(
                c1,
                c2
        );

        statsGrid.add(
                totalCard,
                0,
                0
        );

        statsGrid.add(
                pendingCard,
                1,
                0
        );

        statsGrid.add(
                verifiedCard,
                0,
                1
        );

        statsGrid.add(
                rejectedCard,
                1,
                1
        );

        VBox chartCard =
                createChartCard();

        chartCard.setPrefWidth(470);
        chartCard.setMinWidth(430);
        chartCard.setMaxWidth(520);

        section.getChildren().addAll(
                statsGrid,
                chartCard
        );

        return section;
    }


    // ============================================================
    // KPI CARD
    // ============================================================

    private VBox createStatCard(
            String title,
            Label valueLabel,
            String subtext,
            String accentColorHex,
            String backgroundColorHex) {

        VBox card = new VBox(7);

        card.setPadding(
                new Insets(18)
        );

        card.setMinHeight(125);
        card.setPrefHeight(135);

        card.setStyle(
                "-fx-background-color: "
                        + backgroundColorHex
                        + ";"
                        +
                        "-fx-background-radius: 14px;"
                        +
                        "-fx-border-color: "
                        + accentColorHex
                        + ";"
                        +
                        "-fx-border-width: 1px;"
                        +
                        "-fx-border-radius: 14px;"
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
                Color.web("#475569")
        );

        valueLabel.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        27
                )
        );

        valueLabel.setTextFill(
                Color.web(accentColorHex)
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
                Color.web("#64748B")
        );

        subLabel.setWrapText(true);

        card.getChildren().addAll(
                titleLabel,
                valueLabel,
                subLabel
        );

        return card;
    }


    // ============================================================
    // GRAPH CARD
    // ============================================================

    private VBox createChartCard() {

        VBox chartCard =
                new VBox(12);

        chartCard.setPadding(
                new Insets(18)
        );

        chartCard.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-background-radius: 14px;" +
                "-fx-border-color: #E2E8F0;" +
                "-fx-border-radius: 14px;"
        );

        Label chartTitle =
                new Label(
                        "Regional Distribution"
                );

        chartTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        15
                )
        );

        chartTitle.setTextFill(
                Color.web("#0F172A")
        );

        Label chartSubtitle =
                new Label(
                        "Registered healthcare facilities by city"
                );

        chartSubtitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.NORMAL,
                        11
                )
        );

        chartSubtitle.setTextFill(
                Color.web("#64748B")
        );

        CategoryAxis xAxis =
                new CategoryAxis();

        xAxis.setTickLabelFill(
                Color.web("#64748B")
        );

        xAxis.setTickLabelFont(
                Font.font(
                        "Segoe UI",
                        10
                )
        );

        NumberAxis yAxis =
                new NumberAxis();

        yAxis.setTickLabelFill(
                Color.web("#64748B")
        );

        yAxis.setTickLabelFont(
                Font.font(
                        "Segoe UI",
                        10
                )
        );

        yAxis.setForceZeroInRange(true);

        cityBarChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        cityBarChart.setPrefHeight(190);
        cityBarChart.setMinHeight(180);

        cityBarChart.setLegendVisible(false);
        cityBarChart.setAnimated(false);

        cityBarChart.setVerticalGridLinesVisible(false);
        cityBarChart.setHorizontalGridLinesVisible(true);

        cityBarChart.setCategoryGap(18);

        chartCard.getChildren().addAll(
                chartTitle,
                chartSubtitle,
                cityBarChart
        );

        VBox.setVgrow(
                cityBarChart,
                Priority.ALWAYS
        );

        return chartCard;
    }


    // ============================================================
    // FILTER BAR
    // ============================================================

    private HBox createFilterBar() {

        HBox bar =
                new HBox(14);

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
                "🔍 Search Hospital Name, Registration No, or City..."
        );

        searchInput.setPrefWidth(320);

        ComboBox<String> statusFilter =
                new ComboBox<>();

        statusFilter.getItems().addAll(
                "All Statuses",
                "PENDING",
                "VERIFIED",
                "REJECTED"
        );

        statusFilter.setValue(
                "All Statuses"
        );

        /*
         * IMPORTANT:
         *
         * City filter is now a class-level field.
         * This allows loadHospitalData() to populate it.
         */
        cityFilter =
                new ComboBox<>();

        cityFilter.setPrefWidth(160);

        cityFilter.getItems().add(
                "All Cities"
        );

        cityFilter.setValue(
                "All Cities"
        );


        // ========================================================
        // FILTER FUNCTION
        // ========================================================

        Runnable applyFilter = () -> {

            if (filteredData == null) {
                return;
            }

            String query =
                    safe(
                            searchInput.getText()
                    )
                            .toLowerCase()
                            .trim();

            String selectedStatus =
                    statusFilter.getValue();

            String selectedCity =
                    cityFilter.getValue();

            filteredData.setPredicate(
                    hospital -> {

                        if (hospital == null) {
                            return false;
                        }

                        String hospitalName =
                                safe(
                                        hospital.getHospitalName()
                                )
                                        .toLowerCase();

                        String registrationNumber =
                                safe(
                                        hospital.getRegistrationNumber()
                                )
                                        .toLowerCase();

                        String address =
                                safe(
                                        hospital.getAddress()
                                )
                                        .toLowerCase();

                        String city =
                                extractCity(
                                        hospital.getAddress()
                                )
                                        .toLowerCase();

                        boolean matchesQuery =
                                query.isEmpty()
                                        ||
                                        hospitalName.contains(
                                                query
                                        )
                                        ||
                                        registrationNumber.contains(
                                                query
                                        )
                                        ||
                                        address.contains(
                                                query
                                        )
                                        ||
                                        city.contains(
                                                query
                                        );

                        String hospitalStatus =
                                getHospitalStatus(
                                        hospital
                                );

                        boolean matchesStatus =
                                "All Statuses".equals(
                                        selectedStatus
                                )
                                        ||
                                        hospitalStatus.equalsIgnoreCase(
                                                safe(selectedStatus)
                                        );

                        boolean matchesCity =
                                "All Cities".equals(
                                        selectedCity
                                )
                                        ||
                                        city.equalsIgnoreCase(
                                                safe(selectedCity)
                                        );

                        return matchesQuery
                                &&
                                matchesStatus
                                &&
                                matchesCity;
                    }
            );
        };


        searchInput.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilter.run()
                );

        statusFilter.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilter.run()
                );

        cityFilter.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilter.run()
                );


        // ========================================================
        // SPACER
        // ========================================================

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        // ========================================================
        // RESET BUTTON
        // ========================================================

        Button resetBtn =
                new Button(
                        "Reset Filters"
                );

        resetBtn.setStyle(
                "-fx-background-color: #F1F5F9;" +
                "-fx-text-fill: #334155;" +
                "-fx-background-radius: 6px;" +
                "-fx-cursor: hand;"
        );

        resetBtn.setOnAction(
                e -> {

                    searchInput.clear();

                    statusFilter.setValue(
                            "All Statuses"
                    );

                    cityFilter.setValue(
                            "All Cities"
                    );

                    applyFilter.run();
                }
        );


        bar.getChildren().addAll(
                searchInput,
                statusFilter,
                cityFilter,
                spacer,
                resetBtn
        );

        return bar;
    }


    // ============================================================
    // TABLE
    // ============================================================

    private VBox createTableContainer() {

        VBox container =
                new VBox(12);

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
                        "Registered Healthcare Facilities"
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

        hospitalTable =
                new TableView<>();

        hospitalTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        hospitalTable.setPrefHeight(400);


        // ========================================================
        // NAME
        // ========================================================

        TableColumn<HospitalProfile, String> nameCol =
                new TableColumn<>(
                        "Facility Name & Registration"
                );

        nameCol.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                safe(
                                        cellData.getValue()
                                                .getHospitalName()
                                )
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
                            setText(null);

                            return;
                        }

                        HospitalProfile hospital =
                                getTableRow().getItem();

                        if (hospital == null) {

                            setGraphic(null);
                            setText(null);

                            return;
                        }

                        HBox box =
                                new HBox(12);

                        box.setAlignment(
                                Pos.CENTER_LEFT
                        );

                        String firstLetter =
                                name.trim().isEmpty()
                                        ? "H"
                                        : name
                                                .trim()
                                                .substring(0, 1);

                        StackPane icon =
                                createHospitalBadge(
                                        firstLetter
                                );

                        VBox textContainer =
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

                        Label registrationLbl =
                                new Label(
                                        "Reg: "
                                                +
                                                safe(
                                                        hospital.getRegistrationNumber()
                                                )
                                                +
                                                " • "
                                                +
                                                safe(
                                                        hospital.getHospitalType()
                                                )
                                );

                        registrationLbl.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.NORMAL,
                                        11
                                )
                        );

                        registrationLbl.setTextFill(
                                Color.web("#64748B")
                        );

                        textContainer.getChildren().addAll(
                                nameLbl,
                                registrationLbl
                        );

                        box.getChildren().addAll(
                                icon,
                                textContainer
                        );

                        setGraphic(box);
                        setText(null);
                    }
                }
        );


        // ========================================================
        // TYPE
        // ========================================================

        TableColumn<HospitalProfile, String> typeCol =
                new TableColumn<>(
                        "Hospital Type"
                );

        typeCol.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                safe(
                                        cellData.getValue()
                                                .getHospitalType()
                                )
                        )
        );


        // ========================================================
        // BEDS
        // ========================================================

        TableColumn<HospitalProfile, String> bedsCol =
                new TableColumn<>(
                        "Beds"
                );

        bedsCol.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                safe(
                                        cellData.getValue()
                                                .getBeds()
                                )
                        )
        );


        // ========================================================
        // CONTACT
        // ========================================================

        TableColumn<HospitalProfile, String> contactCol =
                new TableColumn<>(
                        "Contact"
                );

        contactCol.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                safe(
                                        cellData.getValue()
                                                .getContact()
                                )
                        )
        );


        // ========================================================
        // ADDRESS
        // ========================================================

        TableColumn<HospitalProfile, String> addressCol =
                new TableColumn<>(
                        "Address"
                );

        addressCol.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                safe(
                                        cellData.getValue()
                                                .getAddress()
                                )
                        )
        );


        // ========================================================
        // STATUS
        // ========================================================

        TableColumn<HospitalProfile, String> statusCol =
                new TableColumn<>(
                        "Verification Status"
                );

        statusCol.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                getHospitalStatus(
                                        cellData.getValue()
                                )
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

                        setText(null);

                        if (empty || status == null) {

                            setGraphic(null);

                            return;
                        }

                        Label badge =
                                new Label(status);

                        badge.setFont(
                                Font.font(
                                        "Segoe UI",
                                        FontWeight.BOLD,
                                        11
                                )
                        );

                        badge.setPadding(
                                new Insets(
                                        5,
                                        12,
                                        5,
                                        12
                                )
                        );

                        applyStatusStyle(
                                badge,
                                status
                        );

                        setGraphic(badge);
                    }
                }
        );


        // ========================================================
        // ACTION
        // ========================================================

        TableColumn<HospitalProfile, HospitalProfile>
                actionCol =
                new TableColumn<>(
                        "Verification Action"
                );

        actionCol.setCellValueFactory(
                cellData ->
                        new ReadOnlyObjectWrapper<>(
                                cellData.getValue()
                        )
        );

        actionCol.setCellFactory(
                col -> new TableCell<>() {

                    private final Button inspectBtn =
                            new Button("View");

                    {

                        inspectBtn.setStyle(
                                "-fx-background-color: #EEF2FF;" +
                                "-fx-text-fill: #4338CA;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 6px;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 7px 14px;"
                        );

                        inspectBtn.setOnAction(
                                e -> {

                                    HospitalProfile hospital =
                                            getTableRow().getItem();

                                    if (hospital != null) {

                                        showDocumentInspectionModal(
                                                hospital
                                        );
                                    }
                                }
                        );
                    }

                    @Override
                    protected void updateItem(
                            HospitalProfile hospital,
                            boolean empty) {

                        super.updateItem(
                                hospital,
                                empty
                        );

                        if (empty || hospital == null) {

                            setGraphic(null);

                        } else {

                            setGraphic(
                                    inspectBtn
                            );
                        }
                    }
                }
        );


        hospitalTable.getColumns().addAll(
                nameCol,
                typeCol,
                bedsCol,
                contactCol,
                addressCol,
                statusCol,
                actionCol
        );

        container.getChildren().addAll(
                tableTitle,
                hospitalTable
        );

        return container;
    }


    // ============================================================
    // HOSPITAL BADGE
    // ============================================================

    private StackPane createHospitalBadge(
            String letter) {

        Circle circle =
                new Circle(16);

        circle.setFill(
                Color.web("#EEF2FF")
        );

        circle.setStroke(
                Color.web("#818CF8")
        );

        circle.setStrokeWidth(1.5);

        Label label =
                new Label(
                        letter.toUpperCase()
                );

        label.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        label.setTextFill(
                Color.web("#4F46E5")
        );

        return new StackPane(
                circle,
                label
        );
    }


    // ============================================================
    // LOAD HOSPITAL DATA
    // ============================================================

    private void loadHospitalData() {

        /*
         * Re-create observable list here.
         */
        masterHospitalData =
                FXCollections.observableArrayList(
                        hospital ->
                                new Observable[]{

                                        new SimpleStringProperty(
                                                safe(
                                                        hospital.getHospitalName()
                                                )
                                        ),

                                        new SimpleStringProperty(
                                                safe(
                                                        hospital.getAddress()
                                                )
                                        ),

                                        new SimpleStringProperty(
                                                safe(
                                                        hospital.getBeds()
                                                )
                                        )
                                }
                );

        try {

            java.util.List<HospitalProfile> hospitals =
                    hospitalDAO.getAllHospitalProfiles();

            verificationMap.clear();

            if (hospitals != null) {

                masterHospitalData.addAll(
                        hospitals
                );

                /*
                 * Load current verification status
                 * directly from Firestore.
                 */
                for (HospitalProfile hospital : hospitals) {

                    loadVerificationForHospital(
                            hospital
                    );
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Failed to load hospitals: "
                            + e.getMessage()
            );

            e.printStackTrace();

            showAlert(
                    "Hospital Data Error",
                    "Unable to load registered hospitals.\n\n"
                            + e.getMessage()
            );
        }

        masterHospitalData.addListener(
                (ListChangeListener<HospitalProfile>)
                        change ->
                                updateCountersAndChart()
        );

        filteredData =
                new FilteredList<>(
                        masterHospitalData,
                        hospital -> true
                );

        hospitalTable.setItems(
                filteredData
        );

        /*
         * Populate the city filter now that the
         * hospital data has been loaded.
         */
        updateCityFilterItems();

        updateCountersAndChart();
    }


    // ============================================================
    // LOAD VERIFICATION
    // ============================================================

    private void loadVerificationForHospital(
            HospitalProfile hospital) {

        if (hospital == null) {
            return;
        }

        String hospitalId =
                getHospitalKey(hospital);

        if (hospitalId.isBlank()) {
            return;
        }

        try {

            HospitalVerification verification =
                    verificationController
                            .getVerificationByHospitalId(
                                    hospitalId
                            );

            if (verification != null) {

                verificationMap.put(
                        hospitalId,
                        verification
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Failed to load verification for hospital "
                            + hospitalId
                            + ": "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // CITY FILTER
    // ============================================================

    private void updateCityFilterItems() {

        if (cityFilter == null) {
            return;
        }

        /*
         * Remember currently selected city.
         */
        String currentSelection =
                cityFilter.getValue();

        /*
         * Get unique cities from hospital addresses.
         */
        ObservableList<String> cities =
                FXCollections.observableArrayList();

        if (masterHospitalData != null) {

            masterHospitalData.stream()
                    .map(
                            hospital ->
                                    extractCity(
                                            hospital.getAddress()
                                    )
                    )
                    .filter(
                            city ->
                                    city != null
                                            &&
                                            !city.isBlank()
                    )
                    .distinct()
                    .sorted(
                            String.CASE_INSENSITIVE_ORDER
                    )
                    .forEach(
                            cities::add
                    );
        }

        /*
         * Rebuild ComboBox items.
         */
        cityFilter.getItems().clear();

        cityFilter.getItems().add(
                "All Cities"
        );

        cityFilter.getItems().addAll(
                cities
        );

        /*
         * Restore previous selection if it still exists.
         */
        if (currentSelection != null
                &&
                cityFilter.getItems().contains(
                        currentSelection
                )) {

            cityFilter.setValue(
                    currentSelection
            );

        } else {

            cityFilter.setValue(
                    "All Cities"
            );
        }
    }


    // ============================================================
    // GET STATUS
    // ============================================================

    private String getHospitalStatus(
            HospitalProfile hospital) {

        if (hospital == null) {

            return "PENDING";
        }

        String hospitalId =
                getHospitalKey(hospital);

        HospitalVerification verification =
                verificationMap.get(hospitalId);

        if (verification != null) {

            String status =
                    verification.getVerificationStatus();

            if (status != null
                    &&
                    !status.isBlank()) {

                return status
                        .trim()
                        .toUpperCase();
            }
        }

        /*
         * If no verification document exists,
         * treat hospital as PENDING.
         */
        return "PENDING";
    }


    // ============================================================
    // HOSPITAL KEY
    // ============================================================

    private String getHospitalKey(
            HospitalProfile hospital) {

        if (hospital == null) {
            return "";
        }

        String uid =
                safe(
                        hospital.getUid()
                ).trim();

        if (!uid.isEmpty()) {
            return uid;
        }

        String registration =
                safe(
                        hospital.getRegistrationNumber()
                ).trim();

        if (!registration.isEmpty()) {
            return registration;
        }

        return safe(
                hospital.getHospitalName()
        ).trim();
    }


    // ============================================================
    // COUNTERS
    // ============================================================

    private void updateCountersAndChart() {

        if (masterHospitalData == null) {
            return;
        }

        int total =
                masterHospitalData.size();

        long pending =
                masterHospitalData.stream()
                        .filter(
                                hospital ->
                                        getHospitalStatus(hospital)
                                                .equalsIgnoreCase(
                                                        "PENDING"
                                                )
                        )
                        .count();

        long verified =
                masterHospitalData.stream()
                        .filter(
                                hospital -> {
                                    String status =
                                            getHospitalStatus(hospital);

                                    return status.equalsIgnoreCase(
                                                "APPROVED"
                                            )
                                            ||
                                            status.equalsIgnoreCase(
                                                "VERIFIED"
                                            );
                                }
                        )
                        .count();

        long rejected =
                masterHospitalData.stream()
                        .filter(
                                hospital ->
                                        getHospitalStatus(hospital)
                                                .equalsIgnoreCase(
                                                        "REJECTED"
                                                )
                        )
                        .count();

        if (totalCountLabel != null) {

            totalCountLabel.setText(
                    String.valueOf(total)
            );
        }

        if (pendingCountLabel != null) {

            pendingCountLabel.setText(
                    String.valueOf(pending)
            );
        }

        if (verifiedCountLabel != null) {

            verifiedCountLabel.setText(
                    String.valueOf(verified)
            );
        }

        if (rejectedCountLabel != null) {

            rejectedCountLabel.setText(
                    String.valueOf(rejected)
            );
        }

        updateCityChart();
    }


    // ============================================================
    // CITY CHART
    // ============================================================

    private void updateCityChart() {

        if (cityBarChart == null) {
            return;
        }

        cityBarChart.getData().clear();

        if (masterHospitalData == null
                ||
                masterHospitalData.isEmpty()) {

            return;
        }

        Map<String, Long> cityCounts =
                masterHospitalData.stream()
                        .collect(
                                Collectors.groupingBy(
                                        hospital ->
                                                extractCity(
                                                        hospital.getAddress()
                                                ),
                                        Collectors.counting()
                                )
                        );

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        cityCounts.forEach(
                (city, count) ->
                        series.getData().add(
                                new XYChart.Data<>(
                                        city,
                                        count
                                )
                        )
        );

        cityBarChart.getData().add(
                series
        );
    }


    // ============================================================
    // STATUS STYLE
    // ============================================================

    private void applyStatusStyle(
            Label badge,
            String status) {

        if (status == null) {
            status = "PENDING";
        }

        switch (
                status.toUpperCase()
        ) {

            case "VERIFIED":

                badge.setStyle(
                        "-fx-background-color: #D1FAE5;" +
                        "-fx-text-fill: #065F46;" +
                        "-fx-background-radius: 20px;"
                );

                break;

            case "REJECTED":

                badge.setStyle(
                        "-fx-background-color: #FEE2E2;" +
                        "-fx-text-fill: #991B1B;" +
                        "-fx-background-radius: 20px;"
                );

                break;

            default:

                badge.setStyle(
                        "-fx-background-color: #FEF3C7;" +
                        "-fx-text-fill: #92400E;" +
                        "-fx-background-radius: 20px;"
                );

                break;
        }
    }


    // ============================================================
    // HOSPITAL INSPECTION MODAL
    // ============================================================

    private void showDocumentInspectionModal(
            HospitalProfile hospital) {

        Dialog<ButtonType> dialog =
                new Dialog<>();

        dialog.setTitle(
                "Hospital Profile Inspection"
        );

        dialog.setHeaderText(
                "Hospital Verification & Credentials"
        );

        ButtonType closeButton =
                new ButtonType(
                        "Close",
                        ButtonBar.ButtonData.CANCEL_CLOSE
                );

        dialog.getDialogPane()
                .getButtonTypes()
                .add(closeButton);

        VBox content =
                new VBox(14);

        content.setPadding(
                new Insets(12)
        );

        content.setPrefWidth(560);

        Label hospitalTitle =
                new Label(
                        safe(
                                hospital.getHospitalName()
                        )
                );

        hospitalTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        19
                )
        );

        hospitalTitle.setTextFill(
                Color.web("#0F172A")
        );


        // ========================================================
        // CURRENT STATUS
        // ========================================================

        String currentStatusValue =
                getHospitalStatus(hospital);

        Label currentStatus =
                new Label(
                        "Current Verification Status: "
                                + currentStatusValue
                );

        currentStatus.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        12
                )
        );

        currentStatus.setPadding(
                new Insets(
                        7,
                        12,
                        7,
                        12
                )
        );

        applyStatusStyle(
                currentStatus,
                currentStatusValue
        );


        // ========================================================
        // DETAILS
        // ========================================================

        Label details =
                new Label(
                        "UID: "
                                + safe(hospital.getUid())

                                + "\n\nEmail: "
                                + safe(hospital.getEmail())

                                + "\n\nRegistration Number: "
                                + safe(
                                        hospital.getRegistrationNumber()
                                )

                                + "\n\nHospital Type: "
                                + safe(
                                        hospital.getHospitalType()
                                )

                                + "\n\nBeds: "
                                + safe(
                                        hospital.getBeds()
                                )

                                + "\n\nContact: "
                                + safe(
                                        hospital.getContact()
                                )

                                + "\n\nAddress: "
                                + safe(
                                        hospital.getAddress()
                                )
                );

        details.setFont(
                Font.font(
                        "Segoe UI",
                        12
                )
        );

        details.setTextFill(
                Color.web("#334155")
        );

        details.setWrapText(true);


        // ========================================================
        // VERIFICATION TITLE
        // ========================================================

        Label verificationTitle =
                new Label(
                        "Change Verification Status:"
                );

        verificationTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        13
                )
        );

        verificationTitle.setTextFill(
                Color.web("#0F172A")
        );


        // ========================================================
        // VERIFY BUTTON
        // ========================================================

        Button verifyBtn =
                new Button(
                        "✓ Verify & Approve"
                );

        styleActionButton(
                verifyBtn,
                "#D1FAE5",
                "#065F46"
        );


        // ========================================================
        // PENDING BUTTON
        // ========================================================

        Button pendingBtn =
                new Button(
                        "⏳ Set as Pending"
                );

        styleActionButton(
                pendingBtn,
                "#FEF3C7",
                "#92400E"
        );


        // ========================================================
        // REJECT BUTTON
        // ========================================================

        Button rejectBtn =
                new Button(
                        "✕ Reject Verification"
                );

        styleActionButton(
                rejectBtn,
                "#FEE2E2",
                "#991B1B"
        );


        // ========================================================
        // VERIFY ACTION
        // ========================================================

        verifyBtn.setOnAction(
                e -> {

                    String verificationId =
                            getVerificationId(
                                    hospital
                            );

                    if (verificationId == null
                            ||
                            verificationId.isBlank()) {

                        showAlert(
                                "Verification Error",
                                "No verification record was found for this hospital."
                        );

                        return;
                    }

                    try {

                        /*
                         * Existing hospitals can be missing a
                         * hospital_verifications document. Create a
                         * PENDING record first, then approve it.
                         */
                        HospitalVerification existingVerification =
                                getVerification(hospital);

                        if (existingVerification == null) {
                            boolean created =
                                    verificationController
                                            .setPendingVerification(
                                                    verificationId,
                                                    ADMIN_UID
                                            );

                            if (!created) {
                                showAlert(
                                        "Verification Failed",
                                        "Unable to create the hospital verification record."
                                );
                                return;
                            }
                        }

                        boolean success =
                                verificationController
                                        .approveVerification(
                                                verificationId,
                                                ADMIN_UID
                                        );

                        if (success) {

                            /*
                             * Reload directly from Firestore.
                             */
                            refreshVerification(
                                    hospital
                            );

                            updateStatusLabel(
                                    currentStatus,
                                    hospital
                            );

                            hospitalTable.refresh();

                            updateCountersAndChart();

                            showAlert(
                                    "Verification Successful",
                                    safe(
                                            hospital.getHospitalName()
                                    )
                                            + " has been verified successfully."
                            );

                        } else {

                            showAlert(
                                    "Verification Failed",
                                    "Unable to verify the hospital."
                            );
                        }

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        showAlert(
                                "Verification Error",
                                ex.getMessage()
                        );
                    }
                }
        );


        // ========================================================
        // PENDING ACTION
        // ========================================================

        pendingBtn.setOnAction(
                e -> {

                    String verificationId =
                            getVerificationId(
                                    hospital
                            );

                    if (verificationId == null
                            ||
                            verificationId.isBlank()) {

                        showAlert(
                                "Verification Error",
                                "No verification record was found for this hospital."
                        );

                        return;
                    }

                    try {

                        /*
                         * Create the verification document when an
                         * older/existing hospital does not have one.
                         * If it already exists, the same operation
                         * updates it to PENDING.
                         */
                        boolean success =
                                verificationController
                                        .setPendingVerification(
                                                verificationId,
                                                ADMIN_UID
                                        );

                        if (success) {

                            refreshVerification(
                                    hospital
                            );

                            updateStatusLabel(
                                    currentStatus,
                                    hospital
                            );

                            hospitalTable.refresh();

                            updateCountersAndChart();

                            showAlert(
                                    "Status Updated",
                                    safe(
                                            hospital.getHospitalName()
                                    )
                                            + " has been moved to PENDING."
                            );

                        } else {

                            showAlert(
                                    "Update Failed",
                                    "Unable to update verification status."
                            );
                        }

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        showAlert(
                                "Status Update Error",
                                ex.getMessage()
                        );
                    }
                }
        );


        // ========================================================
        // REJECT ACTION
        // ========================================================

        rejectBtn.setOnAction(
                e -> {

                    TextInputDialog rejectionDialog =
                            new TextInputDialog();

                    rejectionDialog.setTitle(
                            "Reject Hospital Verification"
                    );

                    rejectionDialog.setHeaderText(
                            "Enter rejection reason"
                    );

                    rejectionDialog.setContentText(
                            "Reason:"
                    );

                    Optional<String> result =
                            rejectionDialog.showAndWait();

                    if (result.isEmpty()) {
                        return;
                    }

                    String reason =
                            result.get().trim();

                    if (reason.isBlank()) {

                        showAlert(
                                "Invalid Reason",
                                "Rejection reason cannot be empty."
                        );

                        return;
                    }

                    String verificationId =
                            getVerificationId(
                                    hospital
                            );

                    if (verificationId == null
                            ||
                            verificationId.isBlank()) {

                        showAlert(
                                "Verification Error",
                                "No verification record was found."
                        );

                        return;
                    }

                    try {

                        /*
                         * A hospital may not yet have a verification
                         * document. Create a PENDING record first so
                         * rejection can safely be applied.
                         */
                        HospitalVerification existingVerification =
                                getVerification(hospital);

                        if (existingVerification == null) {
                            boolean created =
                                    verificationController
                                            .setPendingVerification(
                                                    verificationId,
                                                    ADMIN_UID
                                            );

                            if (!created) {
                                showAlert(
                                        "Rejection Failed",
                                        "Unable to create the hospital verification record."
                                );
                                return;
                            }
                        }

                        boolean success =
                                verificationController
                                        .rejectVerification(
                                                verificationId,
                                                ADMIN_UID,
                                                reason
                                        );

                        if (success) {

                            refreshVerification(
                                    hospital
                            );

                            updateStatusLabel(
                                    currentStatus,
                                    hospital
                            );

                            hospitalTable.refresh();

                            updateCountersAndChart();

                            showAlert(
                                    "Verification Rejected",
                                    safe(
                                            hospital.getHospitalName()
                                    )
                                            + " has been rejected."
                            );

                        } else {

                            showAlert(
                                    "Rejection Failed",
                                    "Unable to reject the hospital verification."
                            );
                        }

                    } catch (Exception ex) {

                        ex.printStackTrace();

                        showAlert(
                                "Rejection Error",
                                ex.getMessage()
                        );
                    }
                }
        );


        // ========================================================
        // ADD CONTENT
        // ========================================================

        content.getChildren().addAll(
                hospitalTitle,
                currentStatus,
                new Separator(),
                details,
                new Separator(),
                verificationTitle,
                verifyBtn,
                pendingBtn,
                rejectBtn
        );

        dialog.getDialogPane()
                .setContent(content);

        dialog.showAndWait();
    }


    // ============================================================
    // ACTION BUTTON STYLE
    // ============================================================

    private void styleActionButton(
            Button button,
            String background,
            String textColor) {

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setPrefHeight(38);

        button.setStyle(
                "-fx-background-color: "
                        + background
                        + ";" +
                "-fx-text-fill: "
                        + textColor
                        + ";" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7px;" +
                "-fx-cursor: hand;"
        );
    }


    // ============================================================
    // UPDATE STATUS LABEL
    // ============================================================

    private void updateStatusLabel(
            Label statusLabel,
            HospitalProfile hospital) {

        String status =
                getHospitalStatus(
                        hospital
                );

        statusLabel.setText(
                "Current Verification Status: "
                        + status
        );

        applyStatusStyle(
                statusLabel,
                status
        );
    }


    // ============================================================
    // GET VERIFICATION
    // ============================================================

    private HospitalVerification getVerification(
            HospitalProfile hospital) {

        if (hospital == null) {
            return null;
        }

        String hospitalId =
                getHospitalKey(
                        hospital
                );

        if (hospitalId.isBlank()) {
            return null;
        }

        /*
         * First check loaded Firestore cache.
         */
        HospitalVerification verification =
                verificationMap.get(
                        hospitalId
                );

        /*
         * If missing, query Firestore.
         */
        if (verification == null) {

            try {

                verification =
                        verificationController
                                .getVerificationByHospitalId(
                                        hospitalId
                                );

                if (verification != null) {

                    verificationMap.put(
                            hospitalId,
                            verification
                    );
                }

            } catch (Exception e) {

                System.err.println(
                        "Failed to retrieve verification: "
                                + e.getMessage()
                );
            }
        }

        return verification;
    }


    // ============================================================
    // GET VERIFICATION ID
    // ============================================================

    private String getVerificationId(
            HospitalProfile hospital) {

        if (hospital == null) {
            return null;
        }

        HospitalVerification verification =
                getVerification(
                        hospital
                );

        /*
         * Existing hospitals may have a users/hospitals document
         * but no hospital_verifications document yet.
         * In that case the hospital UID itself is the verification
         * document ID and the controller will create the missing
         * verification record before changing its status.
         */
        if (verification == null) {
            return getHospitalKey(hospital);
        }

        String verificationId =
                verification.getVerificationId();

        if (verificationId == null
                || verificationId.isBlank()) {
            return getHospitalKey(hospital);
        }

        return verificationId;
    }


    // ============================================================
    // REFRESH VERIFICATION
    // ============================================================

    private void refreshVerification(
            HospitalProfile hospital) {

        if (hospital == null) {
            return;
        }

        String hospitalId =
                getHospitalKey(
                        hospital
                );

        if (hospitalId.isBlank()) {
            return;
        }

        try {

            /*
             * ALWAYS get fresh value from Firestore.
             */
            HospitalVerification verification =
                    verificationController
                            .getVerificationByHospitalId(
                                    hospitalId
                            );

            if (verification != null) {

                verificationMap.put(
                        hospitalId,
                        verification
                );

            } else {

                verificationMap.remove(
                        hospitalId
                );
            }

        } catch (Exception e) {

            System.err.println(
                    "Failed to refresh verification: "
                            + e.getMessage()
            );
        }
    }


    // ============================================================
    // SAFE STRING
    // ============================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }


    // ============================================================
    // EXTRACT CITY
    // ============================================================

    private String extractCity(
            String address) {

        if (address == null
                ||
                address.trim().isEmpty()) {

            return "Unknown";
        }

        String[] parts =
                address.split(",");

        /*
         * Current project convention:
         *
         * First comma-separated section is treated
         * as the city.
         */
        return parts.length > 0
                ?
                parts[0].trim()
                :
                "Unknown";
    }


    // ============================================================
    // ALERT
    // ============================================================

    private void showAlert(
            String title,
            String content) {

        Alert alert =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(
                content == null
                        ?
                        "An unexpected error occurred."
                        :
                        content
        );

        alert.showAndWait();
    }
}