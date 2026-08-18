package com.healthsphere.view.admin;

import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.model.HospitalProfile;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

    private final HospitalDAO hospitalDAO;

    /*
     * Current HospitalProfile does not have a status field.
     * Therefore status is maintained here dynamically.
     *
     * Key  = Hospital UID
     * Value = PENDING / VERIFIED / REJECTED
     */
    private final Map<String, String> hospitalStatusMap =
            new HashMap<>();

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public HospitalManagementView() {
        this(null);
    }

    public HospitalManagementView(Stage stage) {

        this.primaryStage = stage;

        this.hospitalDAO = new HospitalDAO();

        setFitToWidth(true);

        setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background: #F8FAFC;" +
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

        // ========================================================
        // HEADER
        // ========================================================

        HBox header =
                createHeader();

        // ========================================================
        // ANALYTICS
        // ========================================================

        HBox topAnalyticsSection =
                createTopAnalyticsSection();

        // ========================================================
        // FILTERS
        // ========================================================

        HBox filterBar =
                createFilterBar();

        // ========================================================
        // TABLE
        // ========================================================

        VBox tableContainer =
                createTableContainer();

        mainContainer.getChildren().addAll(
                header,
                topAnalyticsSection,
                filterBar,
                tableContainer
        );

        setContent(mainContainer);

        // ========================================================
        // LOAD DATABASE DATA
        // ========================================================

        loadHospitalData();
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

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        VBox titleBox =
                new VBox(4);

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

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        /*
         * REGISTER FACILITY BUTTON REMOVED
         *
         * Only heading remains now.
         */

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

        HBox section =
                new HBox(20);

        section.setAlignment(
                Pos.CENTER
        );

        GridPane statsGrid =
                new GridPane();

        statsGrid.setHgap(16);
        statsGrid.setVgap(16);

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
                        "#4F46E5"
                );

        VBox pendingCard =
                createStatCard(
                        "Pending Verifications",
                        pendingCountLabel,
                        "Action Required",
                        "#D97706"
                );

        VBox verifiedCard =
                createStatCard(
                        "Verified Facilities",
                        verifiedCountLabel,
                        "Compliance Clear",
                        "#059669"
                );

        VBox rejectedCard =
                createStatCard(
                        "Rejected Applications",
                        rejectedCountLabel,
                        "Document Audit Failed",
                        "#DC2626"
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

        // ========================================================
        // CITY CHART
        // ========================================================

        VBox chartCard =
                new VBox(12);

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
                        "Regional Distribution"
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

        cityBarChart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );

        cityBarChart.setPrefHeight(
                180
        );

        cityBarChart.setLegendVisible(
                false
        );

        cityBarChart.setAnimated(
                false
        );

        chartCard.getChildren().addAll(
                chartTitle,
                cityBarChart
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

        VBox card =
                new VBox(6);

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

        // ========================================================
        // SEARCH
        // ========================================================

        TextField searchInput =
                new TextField();

        searchInput.setPromptText(
                "🔍 Search Hospital Name, Registration No, or City..."
        );

        searchInput.setPrefWidth(
                320
        );

        // ========================================================
        // STATUS FILTER
        // ========================================================

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

        // ========================================================
        // CITY FILTER
        // ========================================================

        ComboBox<String> cityFilter =
                new ComboBox<>();

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

                        boolean matchesQuery =
                                query.isEmpty()

                                ||

                                safe(
                                        hospital.getHospitalName()
                                )
                                        .toLowerCase()
                                        .contains(query)

                                ||

                                safe(
                                        hospital.getRegistrationNumber()
                                )
                                        .toLowerCase()
                                        .contains(query)

                                ||

                                safe(
                                        hospital.getAddress()
                                )
                                        .toLowerCase()
                                        .contains(query);

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
                                        selectedStatus
                                );

                        boolean matchesCity =
                                "All Cities".equals(
                                        selectedCity
                                )
                                ||
                                extractCity(
                                        hospital.getAddress()
                                )
                                        .equalsIgnoreCase(
                                                selectedCity
                                        );

                        return matchesQuery
                                && matchesStatus
                                && matchesCity;
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
        // RESET
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

        hospitalTable.setPrefHeight(
                400
        );

        // ========================================================
        // COLUMN 1 - NAME
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
                                getTableRow()
                                        .getItem();

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
                                                .substring(
                                                        0,
                                                        1
                                                );

                        StackPane icon =
                                createHospitalBadge(
                                        firstLetter
                                );

                        VBox textContainer =
                                new VBox(2);

                        Label nameLbl =
                                new Label(
                                        name
                                );

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
                                                + safe(
                                                        hospital
                                                                .getRegistrationNumber()
                                                )
                                                + " • "
                                                + safe(
                                                        hospital
                                                                .getHospitalType()
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
        // COLUMN 2 - TYPE
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
        // COLUMN 3 - BEDS
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
        // COLUMN 4 - CONTACT
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
        // COLUMN 5 - ADDRESS
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
        // COLUMN 6 - STATUS
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
                                new Label(
                                        status
                                );

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
        // COLUMN 7 - ACTIONS
        // ========================================================

        TableColumn<HospitalProfile, HospitalProfile> actionCol =
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

                    private final Button verifyBtn =
                            new Button(
                                    "✓ Verify"
                            );

                    private final Button pendingBtn =
                            new Button(
                                    "⏳ Pending"
                            );

                    private final Button rejectBtn =
                            new Button(
                                    "✕ Reject"
                            );

                    private final HBox btnGroup =
                            new HBox(
                                    6,
                                    verifyBtn,
                                    pendingBtn,
                                    rejectBtn
                            );

                    {
                        btnGroup.setAlignment(
                                Pos.CENTER
                        );

                        // VERIFY
                        verifyBtn.setStyle(
                                "-fx-background-color: #D1FAE5;" +
                                "-fx-text-fill: #065F46;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5px;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 6px 9px;"
                        );

                        // PENDING
                        pendingBtn.setStyle(
                                "-fx-background-color: #FEF3C7;" +
                                "-fx-text-fill: #92400E;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5px;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 6px 9px;"
                        );

                        // REJECT
                        rejectBtn.setStyle(
                                "-fx-background-color: #FEE2E2;" +
                                "-fx-text-fill: #991B1B;" +
                                "-fx-font-size: 11px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 5px;" +
                                "-fx-cursor: hand;" +
                                "-fx-padding: 6px 9px;"
                        );

                        // ====================================================
                        // VERIFY ACTION
                        // ====================================================

                        verifyBtn.setOnAction(
                                e -> {

                                    HospitalProfile hospital =
                                            getTableRow()
                                                    .getItem();

                                    if (hospital != null) {

                                        updateHospitalStatus(
                                                hospital,
                                                "VERIFIED"
                                        );
                                    }
                                }
                        );

                        // ====================================================
                        // PENDING ACTION
                        // ====================================================

                        pendingBtn.setOnAction(
                                e -> {

                                    HospitalProfile hospital =
                                            getTableRow()
                                                    .getItem();

                                    if (hospital != null) {

                                        updateHospitalStatus(
                                                hospital,
                                                "PENDING"
                                        );
                                    }
                                }
                        );

                        // ====================================================
                        // REJECT ACTION
                        // ====================================================

                        rejectBtn.setOnAction(
                                e -> {

                                    HospitalProfile hospital =
                                            getTableRow()
                                                    .getItem();

                                    if (hospital != null) {

                                        updateHospitalStatus(
                                                hospital,
                                                "REJECTED"
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

                        if (
                                empty
                                        || hospital == null
                        ) {

                            setGraphic(null);

                        } else {

                            setGraphic(
                                    btnGroup
                            );
                        }
                    }
                }
        );

        // ========================================================
        // ADD ALL COLUMNS
        // ========================================================

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

        circle.setStrokeWidth(
                1.5
        );

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

        masterHospitalData =
                FXCollections.observableArrayList(
                        hospital ->
                                new Observable[]{
                                        new SimpleStringProperty(
                                                hospital.getHospitalName()
                                        ),
                                        new SimpleStringProperty(
                                                hospital.getAddress()
                                        ),
                                        new SimpleStringProperty(
                                                hospital.getBeds()
                                        )
                                }
                );

        try {

            java.util.List<HospitalProfile> hospitals =
                    hospitalDAO.getAllHospitalProfiles();

            if (hospitals != null) {

                masterHospitalData.addAll(
                        hospitals
                );

                /*
                 * Since current HospitalProfile has no status,
                 * newly loaded records start as PENDING.
                 *
                 * Once Verify/Reject is clicked, the value changes
                 * dynamically.
                 */
                for (
                        HospitalProfile hospital :
                        hospitals
                ) {

                    hospitalStatusMap.putIfAbsent(
                            getHospitalKey(hospital),
                            "PENDING"
                    );
                }
            }

        } catch (Exception e) {

            System.err.println(
                    "Failed to load hospitals: "
                            + e.getMessage()
            );

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

        updateCityFilter();

        updateCountersAndChart();
    }

    // ============================================================
    // CITY FILTER
    // ============================================================

    private void updateCityFilter() {

        /*
         * City is derived from HospitalProfile address.
         *
         * Actual ComboBox is populated dynamically below
         * through the available hospital records.
         */
    }

    // ============================================================
    // STATUS UPDATE
    // ============================================================

    private void updateHospitalStatus(
            HospitalProfile hospital,
            String newStatus) {

        if (hospital == null) {
            return;
        }

        String key =
                getHospitalKey(
                        hospital
                );

        String oldStatus =
                hospitalStatusMap.get(
                        key
                );

        if (newStatus.equalsIgnoreCase(oldStatus)) {
            return;
        }

        hospitalStatusMap.put(
                key,
                newStatus
        );

        /*
         * Refresh table so Status column immediately changes.
         */
        hospitalTable.refresh();

        /*
         * Update dashboard counters.
         */
        updateCountersAndChart();

        /*
         * Show confirmation.
         */
        String hospitalName =
                safe(
                        hospital.getHospitalName()
                );

        String message;

        switch (newStatus) {

            case "VERIFIED":

                message =
                        hospitalName
                                + " has been verified successfully.";

                break;

            case "REJECTED":

                message =
                        hospitalName
                                + " has been rejected.";

                break;

            default:

                message =
                        hospitalName
                                + " has been moved to pending verification.";

                break;
        }

        showAlert(
                "Verification Status Updated",
                message
        );
    }

    // ============================================================
    // GET STATUS
    // ============================================================

    private String getHospitalStatus(
            HospitalProfile hospital) {

        if (hospital == null) {
            return "PENDING";
        }

        return hospitalStatusMap.getOrDefault(
                getHospitalKey(hospital),
                "PENDING"
        );
    }

    // ============================================================
    // UNIQUE HOSPITAL KEY
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
                                        getHospitalStatus(
                                                hospital
                                        )
                                                .equalsIgnoreCase(
                                                        "PENDING"
                                                )
                        )
                        .count();

        long verified =
                masterHospitalData.stream()
                        .filter(
                                hospital ->
                                        getHospitalStatus(
                                                hospital
                                        )
                                                .equalsIgnoreCase(
                                                        "VERIFIED"
                                                )
                        )
                        .count();

        long rejected =
                masterHospitalData.stream()
                        .filter(
                                hospital ->
                                        getHospitalStatus(
                                                hospital
                                        )
                                                .equalsIgnoreCase(
                                                        "REJECTED"
                                                )
                        )
                        .count();

        totalCountLabel.setText(
                String.valueOf(total)
        );

        pendingCountLabel.setText(
                String.valueOf(pending)
        );

        verifiedCountLabel.setText(
                String.valueOf(verified)
        );

        rejectedCountLabel.setText(
                String.valueOf(rejected)
        );

        updateCityChart();
    }

    // ============================================================
    // CITY CHART
    // ============================================================

    private void updateCityChart() {

        cityBarChart.getData().clear();

        if (
                masterHospitalData == null
                        || masterHospitalData.isEmpty()
        ) {

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
    // INSPECTION
    // ============================================================

    private void showDocumentInspectionModal(
            HospitalProfile hospital) {

        Alert dialog =
                new Alert(
                        Alert.AlertType.INFORMATION
                );

        dialog.setTitle(
                "Hospital Profile Inspection"
        );

        dialog.setHeaderText(
                "Hospital: "
                        + safe(
                                hospital.getHospitalName()
                        )
        );

        dialog.setContentText(
                "UID: "
                        + safe(
                                hospital.getUid()
                        )
                        + "\n\nEmail: "
                        + safe(
                                hospital.getEmail()
                        )
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
                        + "\n\nVerification Status: "
                        + getHospitalStatus(
                                hospital
                        )
        );

        dialog.showAndWait();
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private String safe(
            String value) {

        return value == null
                ? ""
                : value;
    }

    private String extractCity(
            String address) {

        if (
                address == null
                        || address.trim().isEmpty()
        ) {

            return "Unknown";
        }

        String[] parts =
                address.split(",");

        return parts.length > 0
                ? parts[0].trim()
                : "Unknown";
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

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                content == null
                        ? "An unexpected error occurred."
                        : content
        );

        alert.showAndWait();
    }
}