package com.healthsphere.view.doctor;

import com.healthsphere.controller.doctor.DoctorAvailabilityController;
import com.healthsphere.model.DoctorAvailability;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.model.UserProfile;
import com.healthsphere.util.Navigation;
import com.healthsphere.util.ResourceImage;
import com.healthsphere.util.SessionManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * Doctor Availability & Schedule
 *
 * Responsibilities:
 *
 * - Doctor working days
 * - Doctor working hours
 * - Appointment duration
 * - Consultation fee
 * - Consultation type
 * - Clinic information
 * - Booking rules
 * - Cancellation rules
 * - Emergency availability
 *
 * Architecture:
 *
 * View
 *   ↓
 * DoctorAvailabilityController
 *   ↓
 * DoctorAvailabilityDAO
 *   ↓
 * Firestore
 *
 * Navigation:
 *
 * - Uses the existing application Stage
 * - Does not create another Stage
 * - Returns a Scene
 * - Uses Navigation.goTo(...)
 */
public class AvailabilityScheduleView {

    // =========================================================
    // COLORS
    // =========================================================

    private static final String PRIMARY_BLUE =
            "#2563EB";

    private static final String PRIMARY_LIGHT =
            "#EFF6FF";

    private static final String DARK_TEXT =
            "#0F172A";

    private static final String SECONDARY_TEXT =
            "#64748B";

    private static final String LIGHT_BACKGROUND =
            "#F8FAFC";

    private static final String CARD_BACKGROUND =
            "#FFFFFF";

    private static final String BORDER =
            "#E2E8F0";

    private static final String SIDEBAR_BACKGROUND =
            "#0F172A";

    private static final String SIDEBAR_TEXT =
            "#94A3B8";

    private static final String SUCCESS_GREEN =
            "#059669";

    private static final String SUCCESS_LIGHT =
            "#ECFDF5";

    private static final String WARNING_ORANGE =
            "#D97706";

    private static final String ERROR_RED =
            "#DC2626";

    // =========================================================
    // STAGE
    // =========================================================

    private final Stage stage;

    private Scene scene;

    // =========================================================
    // CONTROLLER
    // =========================================================

    private final DoctorAvailabilityController controller;

    // =========================================================
    // CURRENT DOCTOR
    // =========================================================

    private String doctorUid;

    private DoctorProfile doctorProfile;

    // =========================================================
    // WORKING DAYS
    // =========================================================

    private final List<DayControls> dayControls =
            new ArrayList<>();

    // =========================================================
    // APPOINTMENT SETTINGS
    // =========================================================

    private ComboBox<String> slotDurationCombo;

    private TextField consultationFeeField;

    private CheckBox inClinicCheckBox;

    private CheckBox videoCheckBox;

    private ComboBox<String> advanceBookingCombo;

    private ComboBox<String> cancellationCombo;

    private CheckBox emergencyCheckBox;

    // =========================================================
    // CLINIC INFORMATION
    // =========================================================

    private TextField clinicNameField;

    private TextField clinicPhoneField;

    private TextField clinicEmailField;

    private TextField clinicAddressField;

    private TextField consultationRoomField;

    // =========================================================
    // HEADER
    // =========================================================

    private Label doctorNameHeader;

    private Label doctorSpecializationHeader;

    private Label statusLabel;

    // =========================================================
    // CALENDAR
    // =========================================================

    private VBox calendarContainer;

    private Label weekLabel;

    private LocalDate currentWeekStart;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AvailabilityScheduleView(
            Stage stage) {

        this.stage = stage;

        this.controller =
                new DoctorAvailabilityController();

        this.currentWeekStart =
                LocalDate.now()
                        .with(
                                TemporalAdjusters
                                        .previousOrSame(
                                                DayOfWeek.MONDAY
                                        )
                        );

        loadCurrentDoctor();

        this.scene =
                createScene();
    }

    // =========================================================
    // GET SCENE
    // =========================================================

    public Scene getScene() {

        return scene;
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    private Scene createScene() {

        BorderPane root =
                new BorderPane();

        root.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        // -----------------------------------------------------
        // SIDEBAR
        // -----------------------------------------------------

        root.setLeft(
                createSidebar()
        );

        // -----------------------------------------------------
        // CONTENT
        // -----------------------------------------------------

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(
                        22,
                        28,
                        35,
                        28
                )
        );

        content.setStyle(
                "-fx-background-color: "
                        + LIGHT_BACKGROUND
                        + ";"
        );

        // -----------------------------------------------------
        // IMPORTANT
        //
        // Create working-day controls BEFORE loading data
        // and before calendar rendering.
        // -----------------------------------------------------

        VBox workingHours =
                createWorkingHoursSection();

        content.getChildren().addAll(

                createTopHeader(),

                createPageHeader(),

                workingHours,

                createWeeklyScheduleCard(),

                createConsultationAndContactSection(),

                createRulesAndEmergencySection()
        );

        // -----------------------------------------------------
        // SCROLL
        // -----------------------------------------------------

        ScrollPane scrollPane =
                new ScrollPane(
                        content
                );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setFitToHeight(
                false
        );

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
                        + "-fx-padding: 0;"
        );

        root.setCenter(
                scrollPane
        );

        // -----------------------------------------------------
        // SCENE
        // -----------------------------------------------------

        double width =
                stage.getWidth() > 0
                        ? stage.getWidth()
                        : 1280;

        double height =
                stage.getHeight() > 0
                        ? stage.getHeight()
                        : 800;

        scene =
                new Scene(
                        root,
                        width,
                        height
                );

        // -----------------------------------------------------
        // LOAD FIRESTORE DATA
        // -----------------------------------------------------

        loadSavedAvailability();

        return scene;
    }

    // =========================================================
    // CURRENT DOCTOR
    // =========================================================

    private void loadCurrentDoctor() {

        SessionManager session =
                SessionManager.getInstance();

        /*
         * IMPORTANT:
         *
         * getCurrentUser() returns UserProfile.
         *
         * Do NOT use AuthenticationResponse here.
         */
        UserProfile currentUser =
                session.getCurrentUser();

        if (currentUser == null) {

            throw new IllegalStateException(
                    "No logged-in user found."
            );
        }

        if (currentUser.getUid() == null
                || currentUser.getUid()
                        .trim()
                        .isEmpty()) {

            throw new IllegalStateException(
                    "Logged-in user UID is missing."
            );
        }

        doctorUid =
                currentUser.getUid()
                        .trim();

        try {

            doctorProfile =
                    controller.getDoctorProfile(
                            doctorUid
                    );

        } catch (Exception e) {

            doctorProfile = null;

            System.err.println(
                    "Unable to load doctor profile: "
                            + e.getMessage()
            );
        }
    }

    // =========================================================
    // LOAD SAVED AVAILABILITY
    // =========================================================

    private void loadSavedAvailability() {

        try {

            DoctorAvailability availability =
                    controller.getAvailability(
                            doctorUid
                    );

            if (availability == null) {

                applyDefaultValues();

                setStatus(
                        "New availability settings",
                        SECONDARY_TEXT
                );

                refreshCalendar();

                return;
            }

            applyAvailabilityToUI(
                    availability
            );

            setStatus(
                    "Saved availability loaded",
                    SUCCESS_GREEN
            );

            refreshCalendar();

        } catch (Exception e) {

            e.printStackTrace();

            applyDefaultValues();

            setStatus(
                    "Using default settings",
                    WARNING_ORANGE
            );

            refreshCalendar();
        }
    }

    // =========================================================
    // DEFAULT VALUES
    // =========================================================

    private void applyDefaultValues() {

        for (DayControls day :
                dayControls) {

            day.checkBox.setSelected(
                    false
            );

            day.startCombo.setValue(
                    "09:00 AM"
            );

            day.endCombo.setValue(
                    "05:00 PM"
            );

            day.startCombo.setDisable(
                    true
            );

            day.endCombo.setDisable(
                    true
            );
        }

        selectDay(
                "Monday",
                true
        );

        selectDay(
                "Tuesday",
                true
        );

        selectDay(
                "Wednesday",
                true
        );

        selectDay(
                "Thursday",
                true
        );

        selectDay(
                "Friday",
                true
        );

        slotDurationCombo.setValue(
                "30 Minutes"
        );

        consultationFeeField.setText(
                "500"
        );

        inClinicCheckBox.setSelected(
                true
        );

        videoCheckBox.setSelected(
                false
        );

        advanceBookingCombo.setValue(
                "7 Days"
        );

        cancellationCombo.setValue(
                "24 Hours"
        );

        emergencyCheckBox.setSelected(
                false
        );

        clinicNameField.setText(
                doctorProfile != null
                        ? safe(
                                doctorProfile
                                        .getHospitalAffiliation()
                        )
                        : ""
        );

        clinicPhoneField.setText(
                doctorProfile != null
                        ? safe(
                                doctorProfile.getPhone()
                        )
                        : ""
        );

        clinicEmailField.setText(
                doctorProfile != null
                        ? safe(
                                doctorProfile.getEmail()
                        )
                        : ""
        );

        clinicAddressField.setText(
                ""
        );

        consultationRoomField.setText(
                ""
        );
    }

    // =========================================================
    // APPLY SAVED AVAILABILITY
    // =========================================================

    private void applyAvailabilityToUI(
            DoctorAvailability availability) {

        setDay(
                "Monday",
                availability.isMondayEnabled(),
                availability.getMondayStartTime(),
                availability.getMondayEndTime()
        );

        setDay(
                "Tuesday",
                availability.isTuesdayEnabled(),
                availability.getTuesdayStartTime(),
                availability.getTuesdayEndTime()
        );

        setDay(
                "Wednesday",
                availability.isWednesdayEnabled(),
                availability.getWednesdayStartTime(),
                availability.getWednesdayEndTime()
        );

        setDay(
                "Thursday",
                availability.isThursdayEnabled(),
                availability.getThursdayStartTime(),
                availability.getThursdayEndTime()
        );

        setDay(
                "Friday",
                availability.isFridayEnabled(),
                availability.getFridayStartTime(),
                availability.getFridayEndTime()
        );

        setDay(
                "Saturday",
                availability.isSaturdayEnabled(),
                availability.getSaturdayStartTime(),
                availability.getSaturdayEndTime()
        );

        setDay(
                "Sunday",
                availability.isSundayEnabled(),
                availability.getSundayStartTime(),
                availability.getSundayEndTime()
        );

        slotDurationCombo.setValue(
                availability.getSlotDurationMinutes()
                        + " Minutes"
        );

        consultationFeeField.setText(
                formatFee(
                        availability.getConsultationFee()
                )
        );

        inClinicCheckBox.setSelected(
                availability.isInClinicAvailable()
        );

        videoCheckBox.setSelected(
                availability
                        .isVideoConsultationAvailable()
        );

        advanceBookingCombo.setValue(
                availability
                        .getAdvanceBookingDays()
                        + " Days"
        );

        cancellationCombo.setValue(
                availability
                        .getCancellationNoticeHours()
                        + " Hours"
        );

        emergencyCheckBox.setSelected(
                availability
                        .isEmergencyAvailability()
        );

        clinicNameField.setText(
                safe(
                        availability.getClinicName()
                )
        );

        clinicPhoneField.setText(
                safe(
                        availability.getClinicPhone()
                )
        );

        clinicEmailField.setText(
                safe(
                        availability.getClinicEmail()
                )
        );

        clinicAddressField.setText(
                safe(
                        availability.getClinicAddress()
                )
        );

        consultationRoomField.setText(
                safe(
                        availability.getConsultationRoom()
                )
        );
    }

    // =========================================================
    // SAVE
    // =========================================================

    private void saveChanges() {

        try {

            validateInput();

            DoctorAvailability availability =
                    buildAvailabilityFromUI();

            controller.saveAvailability(
                    availability
            );

            setStatus(
                    "Changes saved successfully",
                    SUCCESS_GREEN
            );

            refreshCalendar();

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Saved Successfully",
                    "Your availability, appointment fee, "
                            + "consultation information and "
                            + "booking settings were saved."
            );

        } catch (Exception e) {

            e.printStackTrace();

            setStatus(
                    "Save failed",
                    ERROR_RED
            );

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Save",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // VALIDATE INPUT
    // =========================================================

    private void validateInput() {

        if (consultationFeeField == null) {

            throw new IllegalStateException(
                    "Consultation fee field is not initialized."
            );
        }

        String fee =
                consultationFeeField
                        .getText()
                        .trim();

        if (fee.isEmpty()) {

            throw new IllegalArgumentException(
                    "Please enter the consultation fee."
            );
        }

        try {

            double value =
                    Double.parseDouble(
                            fee
                    );

            if (value < 0) {

                throw new IllegalArgumentException(
                        "Consultation fee cannot be negative."
                );
            }

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Please enter a valid consultation fee."
            );
        }

        if (slotDurationCombo.getValue()
                == null) {

            throw new IllegalArgumentException(
                    "Please select appointment duration."
            );
        }

        if (inClinicCheckBox.isSelected()
                == false
                &&
                videoCheckBox.isSelected()
                        == false) {

            throw new IllegalArgumentException(
                    "Select at least one consultation type."
            );
        }

        for (DayControls day :
                dayControls) {

            if (!day.checkBox.isSelected()) {

                continue;
            }

            if (day.startCombo.getValue()
                    == null
                    ||
                    day.endCombo.getValue()
                            == null) {

                throw new IllegalArgumentException(
                        "Please select start and end time for "
                                + day.dayName
                );
            }
        }
    }

    // =========================================================
    // BUILD MODEL
    // =========================================================

    private DoctorAvailability
    buildAvailabilityFromUI() {

        DoctorAvailability availability =
                new DoctorAvailability();

        availability.setDoctorUid(
                doctorUid
        );

        for (DayControls day :
                dayControls) {

            setModelDay(
                    availability,
                    day
            );
        }

        availability.setSlotDurationMinutes(
                parseMinutes(
                        slotDurationCombo.getValue()
                )
        );

        availability.setConsultationFee(
                parseFee(
                        consultationFeeField
                                .getText()
                )
        );

        availability.setInClinicAvailable(
                inClinicCheckBox.isSelected()
        );

        availability.setVideoConsultationAvailable(
                videoCheckBox.isSelected()
        );

        availability.setAdvanceBookingDays(
                parseNumber(
                        advanceBookingCombo.getValue()
                )
        );

        availability
                .setCancellationNoticeHours(
                        parseNumber(
                                cancellationCombo
                                        .getValue()
                        )
                );

        availability.setEmergencyAvailability(
                emergencyCheckBox.isSelected()
        );

        availability.setClinicName(
                safe(
                        clinicNameField.getText()
                )
        );

        availability.setClinicPhone(
                safe(
                        clinicPhoneField.getText()
                )
        );

        availability.setClinicEmail(
                safe(
                        clinicEmailField.getText()
                )
        );

        availability.setClinicAddress(
                safe(
                        clinicAddressField.getText()
                )
        );

        availability.setConsultationRoom(
                safe(
                        consultationRoomField.getText()
                )
        );

        return availability;
    }

    // =========================================================
    // SET MODEL DAY
    // =========================================================

    private void setModelDay(
            DoctorAvailability model,
            DayControls day) {

        boolean enabled =
                day.checkBox.isSelected();

        String start =
                enabled
                        ? safe(
                                day.startCombo
                                        .getValue()
                        )
                        : "";

        String end =
                enabled
                        ? safe(
                                day.endCombo
                                        .getValue()
                        )
                        : "";

        switch (
                day.dayName.toLowerCase()
        ) {

            case "monday":

                model.setMondayEnabled(
                        enabled
                );

                model.setMondayStartTime(
                        start
                );

                model.setMondayEndTime(
                        end
                );

                break;

            case "tuesday":

                model.setTuesdayEnabled(
                        enabled
                );

                model.setTuesdayStartTime(
                        start
                );

                model.setTuesdayEndTime(
                        end
                );

                break;

            case "wednesday":

                model.setWednesdayEnabled(
                        enabled
                );

                model.setWednesdayStartTime(
                        start
                );

                model.setWednesdayEndTime(
                        end
                );

                break;

            case "thursday":

                model.setThursdayEnabled(
                        enabled
                );

                model.setThursdayStartTime(
                        start
                );

                model.setThursdayEndTime(
                        end
                );

                break;

            case "friday":

                model.setFridayEnabled(
                        enabled
                );

                model.setFridayStartTime(
                        start
                );

                model.setFridayEndTime(
                        end
                );

                break;

            case "saturday":

                model.setSaturdayEnabled(
                        enabled
                );

                model.setSaturdayStartTime(
                        start
                );

                model.setSaturdayEndTime(
                        end
                );

                break;

            case "sunday":

                model.setSundayEnabled(
                        enabled
                );

                model.setSundayStartTime(
                        start
                );

                model.setSundayEndTime(
                        end
                );

                break;

            default:
                break;
        }
    }

    // =========================================================
    // WORKING HOURS SECTION
    // =========================================================

    private VBox createWorkingHoursSection() {

        VBox card =
                createCard();

        Label title =
                createSectionTitle(
                        "Working Hours"
                );

        Label subtitle =
                new Label(
                        "Select the days and hours when patients "
                                + "can request appointments."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        VBox days =
                new VBox(8);

        createDayControls(
                "Monday",
                days
        );

        createDayControls(
                "Tuesday",
                days
        );

        createDayControls(
                "Wednesday",
                days
        );

        createDayControls(
                "Thursday",
                days
        );

        createDayControls(
                "Friday",
                days
        );

        createDayControls(
                "Saturday",
                days
        );

        createDayControls(
                "Sunday",
                days
        );

        card.getChildren().addAll(
                title,
                subtitle,
                days
        );

        return card;
    }

    // =========================================================
    // CREATE DAY CONTROLS
    // =========================================================

    private void createDayControls(
            String dayName,
            VBox container) {

        HBox row =
                new HBox(12);

        row.setAlignment(
                Pos.CENTER_LEFT
        );

        CheckBox checkBox =
                new CheckBox(
                        dayName
                );

        checkBox.setPrefWidth(
                100
        );

        checkBox.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        ComboBox<String> start =
                createTimeCombo();

        ComboBox<String> end =
                createTimeCombo();

        start.setValue(
                "09:00 AM"
        );

        end.setValue(
                "05:00 PM"
        );

        start.setDisable(
                true
        );

        end.setDisable(
                true
        );

        checkBox.selectedProperty()
                .addListener(
                        (observable,
                         oldValue,
                         newValue) -> {

                            start.setDisable(
                                    !newValue
                            );

                            end.setDisable(
                                    !newValue
                            );

                            refreshCalendar();
                        }
                );

        Label to =
                new Label(
                        "to"
                );

        to.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
        );

        row.getChildren().addAll(
                checkBox,
                start,
                to,
                end
        );

        dayControls.add(
                new DayControls(
                        dayName,
                        checkBox,
                        start,
                        end
                )
        );

        container.getChildren().add(
                row
        );
    }

    // =========================================================
    // CONSULTATION + CONTACT
    // =========================================================

    private HBox createConsultationAndContactSection() {

        HBox section =
                new HBox(18);

        VBox consultation =
                createConsultationCard();

        VBox contact =
                createContactCard();

        HBox.setHgrow(
                consultation,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                contact,
                Priority.ALWAYS
        );

        section.getChildren().addAll(
                consultation,
                contact
        );

        return section;
    }

    // =========================================================
    // CONSULTATION CARD
    // =========================================================

    private VBox createConsultationCard() {

        VBox card =
                createCard();

        card.setMinWidth(
                400
        );

        card.getChildren().add(
                createSectionTitle(
                        "Consultation Settings"
                )
        );

        Label information =
                new Label(
                        "These details will be shown to patients "
                                + "when they select your profile."
                );

        information.setWrapText(
                true
        );

        information.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        consultationFeeField =
                createTextField(
                        "500"
                );

        slotDurationCombo =
                new ComboBox<>();

        slotDurationCombo.getItems()
                .addAll(
                        "15 Minutes",
                        "30 Minutes",
                        "45 Minutes",
                        "60 Minutes"
                );

        slotDurationCombo.setValue(
                "30 Minutes"
        );

        styleCombo(
                slotDurationCombo
        );

        inClinicCheckBox =
                new CheckBox(
                        "In-Clinic"
                );

        videoCheckBox =
                new CheckBox(
                        "Video Consultation"
                );

        inClinicCheckBox.setSelected(
                true
        );

        inClinicCheckBox.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        videoCheckBox.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
        );

        HBox consultationTypes =
                new HBox(
                        20,
                        inClinicCheckBox,
                        videoCheckBox
                );

        GridPane form =
                createFormGrid();

        addFormRow(
                form,
                "Consultation Fee (₹)",
                consultationFeeField,
                0
        );

        addFormRow(
                form,
                "Appointment Duration",
                slotDurationCombo,
                1
        );

        addFormRow(
                form,
                "Consultation Type",
                consultationTypes,
                2
        );

        card.getChildren().addAll(
                information,
                form
        );

        return card;
    }

    // =========================================================
    // CONTACT CARD
    // =========================================================

    private VBox createContactCard() {

        VBox card =
                createCard();

        card.setMinWidth(
                400
        );

        card.getChildren().add(
                createSectionTitle(
                        "Clinic & Contact Information"
                )
        );

        Label information =
                new Label(
                        "Patients can see these details before "
                                + "booking an appointment."
                );

        information.setWrapText(
                true
        );

        information.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        clinicNameField =
                createTextField(
                        ""
                );

        clinicPhoneField =
                createTextField(
                        ""
                );

        clinicEmailField =
                createTextField(
                        ""
                );

        clinicAddressField =
                createTextField(
                        ""
                );

        consultationRoomField =
                createTextField(
                        ""
                );

        GridPane form =
                createFormGrid();

        addFormRow(
                form,
                "Clinic / Hospital",
                clinicNameField,
                0
        );

        addFormRow(
                form,
                "Phone",
                clinicPhoneField,
                1
        );

        addFormRow(
                form,
                "Email",
                clinicEmailField,
                2
        );

        addFormRow(
                form,
                "Address",
                clinicAddressField,
                3
        );

        addFormRow(
                form,
                "Consultation Room",
                consultationRoomField,
                4
        );

        card.getChildren().addAll(
                information,
                form
        );

        return card;
    }

    // =========================================================
    // RULES + EMERGENCY
    // =========================================================

    private HBox createRulesAndEmergencySection() {

        HBox section =
                new HBox(18);

        VBox rules =
                createRulesCard();

        VBox emergency =
                createEmergencyCard();

        HBox.setHgrow(
                rules,
                Priority.ALWAYS
        );

        section.getChildren().addAll(
                rules,
                emergency
        );

        return section;
    }

    // =========================================================
    // RULES CARD
    // =========================================================

    private VBox createRulesCard() {

        VBox card =
                createCard();

        card.getChildren().add(
                createSectionTitle(
                        "Booking & Cancellation Rules"
                )
        );

        Label information =
                new Label(
                        "Configure how patients can book "
                                + "appointments."
                );

        information.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        advanceBookingCombo =
                new ComboBox<>();

        advanceBookingCombo.getItems()
                .addAll(
                        "1 Days",
                        "3 Days",
                        "7 Days",
                        "14 Days",
                        "30 Days"
                );

        advanceBookingCombo.setValue(
                "7 Days"
        );

        styleCombo(
                advanceBookingCombo
        );

        cancellationCombo =
                new ComboBox<>();

        cancellationCombo.getItems()
                .addAll(
                        "0 Hours",
                        "6 Hours",
                        "12 Hours",
                        "24 Hours",
                        "48 Hours"
                );

        cancellationCombo.setValue(
                "24 Hours"
        );

        styleCombo(
                cancellationCombo
        );

        GridPane form =
                createFormGrid();

        addFormRow(
                form,
                "Advance Booking",
                advanceBookingCombo,
                0
        );

        addFormRow(
                form,
                "Cancellation Notice",
                cancellationCombo,
                1
        );

        card.getChildren().addAll(
                information,
                form
        );

        return card;
    }

    // =========================================================
    // EMERGENCY CARD
    // =========================================================

    private VBox createEmergencyCard() {

        VBox card =
                createCard();

        card.setPrefWidth(
                350
        );

        Label title =
                createSectionTitle(
                        "Emergency Availability"
                );

        Label information =
                new Label(
                        "Allow patients to request urgent "
                                + "appointments outside regular hours."
                );

        information.setWrapText(
                true
        );

        information.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        emergencyCheckBox =
                new CheckBox(
                        "Accept Emergency Requests"
                );

        emergencyCheckBox.setStyle(
                "-fx-text-fill: "
                        + WARNING_ORANGE
                        + ";"
                        + "-fx-font-weight: bold;"
        );

        card.getChildren().addAll(
                title,
                information,
                emergencyCheckBox
        );

        return card;
    }

    // =========================================================
    // WEEKLY CALENDAR
    // =========================================================

    private VBox createWeeklyScheduleCard() {

        VBox card =
                createCard();

        BorderPane header =
                new BorderPane();

        VBox titleBox =
                new VBox(3);

        Label title =
                createSectionTitle(
                        "Weekly Availability Preview"
                );

        Label subtitle =
                new Label(
                        "Preview of the availability that patients "
                                + "will use when selecting appointment slots."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        HBox navigation =
                new HBox(7);

        Button previous =
                createSmallButton(
                        "‹"
                );

        Button today =
                createSmallButton(
                        "Today"
                );

        Button next =
                createSmallButton(
                        "›"
                );

        weekLabel =
                new Label();

        weekLabel.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 12px;"
        );

        previous.setOnAction(
                event -> {

                    currentWeekStart =
                            currentWeekStart
                                    .minusWeeks(
                                            1
                                    );

                    refreshCalendar();
                }
        );

        today.setOnAction(
                event -> {

                    currentWeekStart =
                            LocalDate.now()
                                    .with(
                                            TemporalAdjusters
                                                    .previousOrSame(
                                                            DayOfWeek.MONDAY
                                                    )
                                    );

                    refreshCalendar();
                }
        );

        next.setOnAction(
                event -> {

                    currentWeekStart =
                            currentWeekStart
                                    .plusWeeks(
                                            1
                                    );

                    refreshCalendar();
                }
        );

        navigation.getChildren()
                .addAll(
                        previous,
                        weekLabel,
                        today,
                        next
                );

        header.setLeft(
                titleBox
        );

        header.setRight(
                navigation
        );

        calendarContainer =
                new VBox(8);

        card.getChildren().addAll(
                header,
                calendarContainer
        );

        return card;
    }

    // =========================================================
    // REFRESH CALENDAR
    // =========================================================

    private void refreshCalendar() {

        if (calendarContainer == null) {

            return;
        }

        calendarContainer
                .getChildren()
                .clear();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "MMM d"
                );

        LocalDate end =
                currentWeekStart
                        .plusDays(
                                6
                        );

        if (weekLabel != null) {

            weekLabel.setText(
                    currentWeekStart
                            .format(
                                    formatter
                            )
                            + " - "
                            + end.format(
                                    formatter
                            )
            );
        }

        GridPane grid =
                new GridPane();

        grid.setHgap(
                1
        );

        grid.setVgap(
                1
        );

        grid.setStyle(
                "-fx-background-color: "
                        + BORDER
                        + ";"
        );

        ColumnConstraints timeColumn =
                new ColumnConstraints();

        timeColumn.setPrefWidth(
                80
        );

        grid.getColumnConstraints()
                .add(
                        timeColumn
                );

        for (int i = 0;
             i < 7;
             i++) {

            ColumnConstraints column =
                    new ColumnConstraints();

            column.setHgrow(
                    Priority.ALWAYS
            );

            grid.getColumnConstraints()
                    .add(
                            column
                    );
        }

        grid.add(
                createCalendarHeader(
                        ""
                ),
                0,
                0
        );

        for (int i = 0;
             i < 7;
             i++) {

            LocalDate date =
                    currentWeekStart
                            .plusDays(
                                    i
                            );

            String day =
                    capitalize(
                            date.getDayOfWeek()
                                    .toString()
                                    .toLowerCase()
                    );

            String shortDay =
                    day.substring(
                            0,
                            3
                    );

            String text =
                    shortDay
                            + "\n"
                            + date.format(
                                    formatter
                            );

            Label header =
                    createCalendarHeader(
                            text
                    );

            if (date.equals(
                    LocalDate.now()
            )) {

                header.setStyle(
                        "-fx-background-color: "
                                + PRIMARY_LIGHT
                                + ";"
                                + "-fx-text-fill: "
                                + PRIMARY_BLUE
                                + ";"
                                + "-fx-font-weight: bold;"
                                + "-fx-padding: 10;"
                );
            }

            grid.add(
                    header,
                    i + 1,
                    0
            );
        }

        String[] times = {
                "8 AM",
                "9 AM",
                "10 AM",
                "11 AM",
                "12 PM",
                "1 PM",
                "2 PM",
                "3 PM",
                "4 PM",
                "5 PM"
        };

        for (int row = 0;
             row < times.length;
             row++) {

            Label time =
                    new Label(
                            times[row]
                    );

            time.setStyle(
                    "-fx-background-color: white;"
                            + "-fx-text-fill: "
                            + SECONDARY_TEXT
                            + ";"
                            + "-fx-font-size: 11px;"
                            + "-fx-padding: 10;"
            );

            grid.add(
                    time,
                    0,
                    row + 1
            );

            for (int column = 0;
                 column < 7;
                 column++) {

                LocalDate date =
                        currentWeekStart
                                .plusDays(
                                        column
                                );

                VBox cell =
                        createCalendarCell(
                                date
                        );

                grid.add(
                        cell,
                        column + 1,
                        row + 1
                );
            }
        }

        calendarContainer
                .getChildren()
                .add(
                        grid
                );

        calendarContainer
                .getChildren()
                .add(
                        createCalendarLegend()
                );
    }

    // =========================================================
    // CALENDAR CELL
    // =========================================================

    private VBox createCalendarCell(
            LocalDate date) {

        VBox cell =
                new VBox(3);

        cell.setMinHeight(
                42
        );

        cell.setPadding(
                new Insets(
                        6
                )
        );

        DayControls day =
                findDay(
                        capitalize(
                                date.getDayOfWeek()
                                        .toString()
                                        .toLowerCase()
                        )
                );

        if (day != null
                && day.checkBox
                        .isSelected()) {

            cell.setStyle(
                    "-fx-background-color: "
                            + SUCCESS_LIGHT
                            + ";"
            );

            Label available =
                    new Label(
                            "Available"
                    );

            available.setStyle(
                    "-fx-text-fill: "
                            + SUCCESS_GREEN
                            + ";"
                            + "-fx-font-size: 10px;"
                            + "-fx-font-weight: bold;"
            );

            cell.getChildren().add(
                    available
            );

        } else {

            cell.setStyle(
                    "-fx-background-color: white;"
            );

            Label off =
                    new Label(
                            "Off"
                    );

            off.setStyle(
                    "-fx-text-fill: #94A3B8;"
                            + "-fx-font-size: 10px;"
            );

            cell.getChildren().add(
                    off
            );
        }

        return cell;
    }

    // =========================================================
    // CALENDAR LEGEND
    // =========================================================

    private HBox createCalendarLegend() {

        HBox legend =
                new HBox(18);

        legend.setAlignment(
                Pos.CENTER_LEFT
        );

        legend.getChildren().addAll(
                createLegendItem(
                        SUCCESS_GREEN,
                        "Working day"
                ),
                createLegendItem(
                        "#94A3B8",
                        "Day off"
                )
        );

        return legend;
    }

    private HBox createLegendItem(
            String color,
            String text) {

        HBox item =
                new HBox(6);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        Circle circle =
                new Circle(
                        4,
                        Color.web(
                                color
                        )
                );

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 11px;"
        );

        item.getChildren().addAll(
                circle,
                label
        );

        return item;
    }

    // =========================================================
    // TOP HEADER
    // =========================================================

    private HBox createTopHeader() {

        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        Label doctor =
                new Label(
                        "Doctor"
                );

        doctor.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        Label separator =
                new Label(
                        "  ›  "
                );

        separator.setStyle(
                "-fx-text-fill: #CBD5E1;"
        );

        Label page =
                new Label(
                        "Availability & Schedule"
                );

        page.setStyle(
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        Region spacer =
                new Region();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );

        Circle avatar =
                new Circle(
                        19
                );

        avatar.setFill(
                Color.web(
                        PRIMARY_LIGHT
                )
        );

        Label initials =
                new Label(
                        getDoctorInitials()
                );

        initials.setStyle(
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        StackPane avatarBox =
                new StackPane(
                        avatar,
                        initials
                );

        VBox doctorBox =
                new VBox(1);

        doctorNameHeader =
                new Label(
                        getDoctorDisplayName()
                );

        doctorNameHeader.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );

        doctorSpecializationHeader =
                new Label(
                        getDoctorSpecialization()
                );

        doctorSpecializationHeader.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 10px;"
        );

        doctorBox.getChildren().addAll(
                doctorNameHeader,
                doctorSpecializationHeader
        );

        header.getChildren().addAll(
                doctor,
                separator,
                page,
                spacer,
                avatarBox,
                doctorBox
        );

        return header;
    }

    // =========================================================
    // PAGE HEADER
    // =========================================================

    private BorderPane createPageHeader() {

        BorderPane header =
                new BorderPane();

        VBox titleBox =
                new VBox(5);

        Label title =
                new Label(
                        "Schedule Management"
                );

        title.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-size: 25px;"
                        + "-fx-font-weight: bold;"
        );

        Label subtitle =
                new Label(
                        "Manage your appointment availability "
                                + "and information visible to patients."
                );

        subtitle.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 13px;"
        );

        statusLabel =
                new Label(
                        "Loading..."
                );

        statusLabel.setStyle(
                "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle,
                statusLabel
        );

        HBox actions =
                new HBox(8);

        Button refresh =
                createSecondaryButton(
                        "Refresh"
                );

        refresh.setOnAction(
                event ->
                        loadSavedAvailability()
        );

        Button save =
                createPrimaryButton(
                        "Save Changes"
                );

        save.setOnAction(
                event ->
                        saveChanges()
        );

        actions.getChildren().addAll(
                refresh,
                save
        );

        header.setLeft(
                titleBox
        );

        header.setRight(
                actions
        );

        return header;
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private VBox createSidebar() {

        VBox sidebar =
                new VBox(6);

        sidebar.setPrefWidth(
                250
        );

        sidebar.setMinWidth(
                250
        );

        sidebar.setMaxWidth(
                250
        );

        sidebar.setPadding(
                new Insets(
                        24,
                        15,
                        20,
                        15
                )
        );

        sidebar.setStyle(
                "-fx-background-color: "
                        + SIDEBAR_BACKGROUND
                        + ";"
        );

        // -----------------------------------------------------
        // LOGO
        // -----------------------------------------------------

        HBox logo =
                new HBox(10);

        logo.setAlignment(
                Pos.CENTER_LEFT
        );

        StackPane logoIcon =
                new StackPane();

        logoIcon.setPrefSize(
                40,
                40
        );

        logoIcon.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-background-radius: 9;"
        );

        Label plus =
                new Label(
                        "+"
                );

        plus.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 24px;"
                        + "-fx-font-weight: bold;"
        );

        logoIcon.getChildren().add(
                plus
        );

        VBox logoText =
                new VBox(1);

        Label appName =
                new Label(
                        "Health-Sphere"
                );

        appName.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 17px;"
                        + "-fx-font-weight: bold;"
        );

        Label dashboard =
                new Label(
                        "Doctor Dashboard"
                );

        dashboard.setStyle(
                "-fx-text-fill: "
                        + SIDEBAR_TEXT
                        + ";"
                        + "-fx-font-size: 10px;"
        );

        logoText.getChildren().addAll(
                appName,
                dashboard
        );

        logo.getChildren().addAll(
                logoIcon,
                logoText
        );

        sidebar.getChildren().add(
                logo
        );

        // -----------------------------------------------------
        // NAVIGATION
        // -----------------------------------------------------

        VBox navigation =
                new VBox(5);

        navigation.setPadding(
                new Insets(
                        25,
                        0,
                        0,
                        0
                )
        );

        String[] names = {
                "Dashboard",
                "Today's Schedule",
                "Appointments",
                "Patient Details",
                "Medical Reports & Prescription",
                "Availability & Schedule",
                "Doctor Profile",
                "AI Health Assistant"
        };

        String[] icons = {
                "ic_dashboard",
                "ic_schedule",
                "ic_appointments",
                "ic_patient",
                "ic_reports",
                "ic_availability",
                "ic_profile",
                "ic_ai"
        };

        for (int i = 0;
             i < names.length;
             i++) {

            final int index =
                    i;

            HBox item =
                    createNavigationItem(
                            names[i],
                            icons[i],
                            i == 5
                    );

            item.setOnMouseClicked(
                    event ->
                            handleSidebarTabClick(
                                    index
                            )
            );

            navigation.getChildren()
                    .add(
                            item
                    );
        }

        sidebar.getChildren().add(
                navigation
        );

        // -----------------------------------------------------
        // SPACER
        // -----------------------------------------------------

        Region spacer =
                new Region();

        VBox.setVgrow(
                spacer,
                Priority.ALWAYS
        );

        sidebar.getChildren().add(
                spacer
        );

        // -----------------------------------------------------
        // PROFILE
        // -----------------------------------------------------

        HBox profile =
                new HBox(10);

        profile.setAlignment(
                Pos.CENTER_LEFT
        );

        profile.setPadding(
                new Insets(
                        10
                )
        );

        profile.setStyle(
                "-fx-background-color: #1E293B;"
                        + "-fx-background-radius: 9;"
                        + "-fx-cursor: hand;"
        );

        Circle circle =
                new Circle(
                        18
                );

        circle.setFill(
                Color.web(
                        PRIMARY_LIGHT
                )
        );

        Label initials =
                new Label(
                        getDoctorInitials()
                );

        initials.setStyle(
                "-fx-text-fill: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 11px;"
        );

        StackPane profileAvatar =
                new StackPane(
                        circle,
                        initials
                );

        VBox profileText =
                new VBox(2);

        Label role =
                new Label(
                        "Doctor Profile"
                );

        role.setStyle(
                "-fx-text-fill: "
                        + SIDEBAR_TEXT
                        + ";"
                        + "-fx-font-size: 9px;"
        );

        Label name =
                new Label(
                        getDoctorDisplayName()
                );

        name.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        profileText.getChildren().addAll(
                role,
                name
        );

        profile.getChildren().addAll(
                profileAvatar,
                profileText
        );

        profile.setOnMouseClicked(
                event ->
                        Navigation.goTo(
                                stage,
                                () ->
                                        new DoctorProfileView(
                                                stage
                                        ).getScene()
                        )
        );

        sidebar.getChildren().add(
                profile
        );

        // -----------------------------------------------------
        // LOGOUT
        // -----------------------------------------------------

        HBox logout =
                new HBox(12);

        logout.setAlignment(
                Pos.CENTER_LEFT
        );

        logout.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        logout.setStyle(
                "-fx-cursor: hand;"
        );

        Label logoutIcon =
                new Label(
                        "↪"
                );

        logoutIcon.setStyle(
                "-fx-text-fill: "
                        + SIDEBAR_TEXT
                        + ";"
                        + "-fx-font-size: 17px;"
        );

        Label logoutText =
                new Label(
                        "Logout"
                );

        logoutText.setStyle(
                "-fx-text-fill: "
                        + SIDEBAR_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
        );

        logout.getChildren().addAll(
                logoutIcon,
                logoutText
        );

        logout.setOnMouseClicked(
                event ->
                        handleLogout()
        );

        sidebar.getChildren().add(
                logout
        );

        return sidebar;
    }

    // =========================================================
    // NAVIGATION ITEM
    // =========================================================

    private HBox createNavigationItem(
            String text,
            String iconName,
            boolean selected) {

        HBox item =
                new HBox(11);

        item.setAlignment(
                Pos.CENTER_LEFT
        );

        item.setPadding(
                new Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        if (selected) {

            item.setStyle(
                    "-fx-background-color: "
                            + PRIMARY_BLUE
                            + ";"
                            + "-fx-background-radius: 8;"
                            + "-fx-cursor: hand;"
            );

        } else {

            item.setStyle(
                    "-fx-background-color: transparent;"
                            + "-fx-background-radius: 8;"
                            + "-fx-cursor: hand;"
            );
        }

        ImageView icon;

        try {

            icon =
                    new ImageView(
                            ResourceImage.load(
                                    "/images/icons/"
                                            + iconName
                                            + ".png"
                            )
                    );

        } catch (Exception e) {

            icon =
                    new ImageView();
        }

        icon.setFitWidth(
                17
        );

        icon.setFitHeight(
                17
        );

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-text-fill: "
                        + (
                        selected
                                ? "white"
                                : SIDEBAR_TEXT
                )
                        + ";"
                        + "-fx-font-size: 12px;"
                        + (
                        selected
                                ? "-fx-font-weight: bold;"
                                : ""
                )
        );

        item.getChildren().addAll(
                icon,
                label
        );

        return item;
    }

    // =========================================================
    // SIDEBAR NAVIGATION
    // =========================================================

    private void handleSidebarTabClick(
            int index) {

        try {

            switch (index) {

                case 0:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new DoctorDashboardView(
                                            stage
                                    ).getScene()
                    );

                    break;

                case 1:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new TodaysScheduleView(
                                            stage
                                    ).getScene()
                    );

                    break;

                case 2:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new AppointmentsView(
                                            stage
                                    ).getScene()
                    );

                    break;

                case 3:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new PatientDetailsView(
                                            stage
                                    ).getScene()
                    );

                    break;

                case 4:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new MedicalReportsView(
                                            stage
                                    ).getScene()
                    );

                    break;

                case 5:

                    break;

                case 6:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new DoctorProfileView(
                                            stage
                                    ).getScene()
                    );

                    break;

                case 7:

                    Navigation.goTo(
                            stage,
                            () ->
                                    new AIHealthAssistantView(
                                            stage
                                    ).getScene()
                    );

                    break;

                default:

                    break;
            }

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Navigation Error",
                    getRootMessage(e)
            );
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void handleLogout() {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        alert.setTitle(
                "Logout"
        );

        alert.setHeaderText(
                "Logout from Health-Sphere?"
        );

        alert.setContentText(
                "Are you sure you want to logout?"
        );

        alert.showAndWait()
                .ifPresent(
                        result -> {

                            if (result
                                    == ButtonType.OK) {

                                SessionManager
                                        .getInstance()
                                        .clearSession();

                                stage.close();
                            }
                        }
                );
    }

    // =========================================================
    // FIND DAY
    // =========================================================

    private DayControls findDay(
            String dayName) {

        for (DayControls day :
                dayControls) {

            if (day.dayName.equalsIgnoreCase(
                    dayName
            )) {

                return day;
            }
        }

        return null;
    }

    // =========================================================
    // SELECT DAY
    // =========================================================

    private void selectDay(
            String dayName,
            boolean enabled) {

        DayControls day =
                findDay(
                        dayName
                );

        if (day == null) {

            return;
        }

        day.checkBox.setSelected(
                enabled
        );

        day.startCombo.setDisable(
                !enabled
        );

        day.endCombo.setDisable(
                !enabled
        );
    }

    // =========================================================
    // SET DAY
    // =========================================================

    private void setDay(
            String dayName,
            boolean enabled,
            String start,
            String end) {

        DayControls day =
                findDay(
                        dayName
                );

        if (day == null) {

            return;
        }

        day.checkBox.setSelected(
                enabled
        );

        day.startCombo.setDisable(
                !enabled
        );

        day.endCombo.setDisable(
                !enabled
        );

        if (start != null
                && !start.isBlank()) {

            selectCombo(
                    day.startCombo,
                    start
            );
        }

        if (end != null
                && !end.isBlank()) {

            selectCombo(
                    day.endCombo,
                    end
            );
        }
    }

    // =========================================================
    // TIME COMBO
    // =========================================================

    private ComboBox<String>
    createTimeCombo() {

        ComboBox<String> combo =
                new ComboBox<>();

        combo.getItems()
                .addAll(
                        createTimeOptions()
                );

        combo.setPrefWidth(
                120
        );

        styleCombo(
                combo
        );

        return combo;
    }

    private List<String>
    createTimeOptions() {

        List<String> values =
                new ArrayList<>();

        for (int hour = 1;
             hour <= 12;
             hour++) {

            for (int minute :
                    new int[]{0, 30}) {

                values.add(
                        String.format(
                                "%02d:%02d AM",
                                hour,
                                minute
                        )
                );
            }
        }

        for (int hour = 1;
             hour <= 12;
             hour++) {

            for (int minute :
                    new int[]{0, 30}) {

                values.add(
                        String.format(
                                "%02d:%02d PM",
                                hour,
                                minute
                        )
                );
            }
        }

        return values;
    }

    // =========================================================
    // SELECT COMBO
    // =========================================================

    private void selectCombo(
            ComboBox<String> combo,
            String value) {

        if (value == null
                || value.isBlank()) {

            return;
        }

        if (combo.getItems()
                .contains(value)) {

            combo.setValue(
                    value
            );
        }
    }

    // =========================================================
    // FORM GRID
    // =========================================================

    private GridPane createFormGrid() {

        GridPane grid =
                new GridPane();

        grid.setHgap(
                15
        );

        grid.setVgap(
                11
        );

        ColumnConstraints first =
                new ColumnConstraints();

        first.setPercentWidth(
                38
        );

        ColumnConstraints second =
                new ColumnConstraints();

        second.setPercentWidth(
                62
        );

        grid.getColumnConstraints()
                .addAll(
                        first,
                        second
                );

        return grid;
    }

    // =========================================================
    // FORM ROW
    // =========================================================

    private void addFormRow(
            GridPane grid,
            String labelText,
            Node control,
            int row) {

        Label label =
                new Label(
                        labelText
                );

        label.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        grid.add(
                label,
                0,
                row
        );

        grid.add(
                control,
                1,
                row
        );

        GridPane.setHgrow(
                control,
                Priority.ALWAYS
        );
    }

    // =========================================================
    // TEXT FIELD
    // =========================================================

    private TextField createTextField(
            String value) {

        TextField field =
                new TextField(
                        value
                );

        field.setPrefHeight(
                37
        );

        field.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 8 10;"
                        + "-fx-font-size: 12px;"
        );

        return field;
    }

    // =========================================================
    // COMBO STYLE
    // =========================================================

    private void styleCombo(
            ComboBox<?> combo) {

        combo.setPrefHeight(
                37
        );

        combo.setMaxWidth(
                Double.MAX_VALUE
        );

        combo.setStyle(
                "-fx-background-color: white;"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-font-size: 12px;"
        );
    }

    // =========================================================
    // CARD
    // =========================================================

    private VBox createCard() {

        VBox card =
                new VBox(12);

        card.setPadding(
                new Insets(
                        18
                )
        );

        card.setStyle(
                "-fx-background-color: "
                        + CARD_BACKGROUND
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 10;"
                        + "-fx-background-radius: 10;"
        );

        return card;
    }

    // =========================================================
    // SECTION TITLE
    // =========================================================

    private Label createSectionTitle(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setStyle(
                "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
        );

        return label;
    }

    // =========================================================
    // PRIMARY BUTTON
    // =========================================================

    private Button createPrimaryButton(
            String text) {

        Button button =
                new Button(
                        text
                );

        button.setStyle(
                "-fx-background-color: "
                        + PRIMARY_BLUE
                        + ";"
                        + "-fx-text-fill: white;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 10 18 10 18;"
                        + "-fx-background-radius: 8;"
                        + "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // SECONDARY BUTTON
    // =========================================================

    private Button createSecondaryButton(
            String text) {

        Button button =
                new Button(
                        text
                );

        button.setStyle(
                "-fx-background-color: white;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 8;"
                        + "-fx-background-radius: 8;"
                        + "-fx-padding: 10 16 10 16;"
                        + "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // SMALL BUTTON
    // =========================================================

    private Button createSmallButton(
            String text) {

        Button button =
                new Button(
                        text
                );

        button.setStyle(
                "-fx-background-color: white;"
                        + "-fx-text-fill: "
                        + DARK_TEXT
                        + ";"
                        + "-fx-border-color: "
                        + BORDER
                        + ";"
                        + "-fx-border-radius: 7;"
                        + "-fx-background-radius: 7;"
                        + "-fx-padding: 6 10 6 10;"
                        + "-fx-font-size: 11px;"
                        + "-fx-cursor: hand;"
        );

        return button;
    }

    // =========================================================
    // CALENDAR HEADER
    // =========================================================

    private Label createCalendarHeader(
            String text) {

        Label label =
                new Label(
                        text
                );

        label.setMaxWidth(
                Double.MAX_VALUE
        );

        label.setAlignment(
                Pos.CENTER
        );

        label.setStyle(
                "-fx-background-color: white;"
                        + "-fx-text-fill: "
                        + SECONDARY_TEXT
                        + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-padding: 9;"
        );

        return label;
    }

    // =========================================================
    // DOCTOR DISPLAY NAME
    // =========================================================

    private String getDoctorDisplayName() {

        if (doctorProfile == null) {

            return "Doctor";
        }

        String first =
                safe(
                        doctorProfile
                                .getFirstName()
                );

        String last =
                safe(
                        doctorProfile
                                .getLastName()
                );

        String fullName =
                (
                        first
                                + " "
                                + last
                ).trim();

        if (fullName.isEmpty()) {

            return "Doctor";
        }

        return "Dr. " + fullName;
    }

    // =========================================================
    // DOCTOR SPECIALIZATION
    // =========================================================

    private String getDoctorSpecialization() {

        if (doctorProfile == null) {

            return "Doctor";
        }

        String specialization =
                safe(
                        doctorProfile
                                .getSpecialization()
                );

        if (specialization.isEmpty()) {

            return "Doctor";
        }

        return specialization;
    }

    // =========================================================
    // INITIALS
    // =========================================================

    private String getDoctorInitials() {

        if (doctorProfile == null) {

            return "DR";
        }

        String first =
                safe(
                        doctorProfile
                                .getFirstName()
                );

        String last =
                safe(
                        doctorProfile
                                .getLastName()
                );

        String initials =
                "";

        if (!first.isEmpty()) {

            initials +=
                    first.substring(
                            0,
                            1
                    );
        }

        if (!last.isEmpty()) {

            initials +=
                    last.substring(
                            0,
                            1
                    );
        }

        if (initials.isEmpty()) {

            return "DR";
        }

        return initials.toUpperCase();
    }

    // =========================================================
    // PARSE MINUTES
    // =========================================================

    private int parseMinutes(
            String value) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment duration is required."
            );
        }

        String number =
                value.replaceAll(
                        "[^0-9]",
                        ""
                );

        return Integer.parseInt(
                number
        );
    }

    // =========================================================
    // PARSE NUMBER
    // =========================================================

    private int parseNumber(
            String value) {

        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    "Required value is missing."
            );
        }

        String number =
                value.replaceAll(
                        "[^0-9]",
                        ""
                );

        return Integer.parseInt(
                number
        );
    }

    // =========================================================
    // PARSE FEE
    // =========================================================

    private double parseFee(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Consultation fee is required."
            );
        }

        try {

            double fee =
                    Double.parseDouble(
                            value.trim()
                    );

            if (fee < 0) {

                throw new IllegalArgumentException(
                        "Consultation fee cannot be negative."
                );
            }

            return fee;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Please enter a valid consultation fee."
            );
        }
    }

    // =========================================================
    // FORMAT FEE
    // =========================================================

    private String formatFee(
            double fee) {

        if (fee == Math.rint(fee)) {

            return String.valueOf(
                    (long) fee
            );
        }

        return String.format(
                "%.2f",
                fee
        );
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(
            String value) {

        if (value == null) {

            return "";
        }

        return value.trim();
    }

    // =========================================================
    // CAPITALIZE
    // =========================================================

    private String capitalize(
            String value) {

        if (value == null
                || value.isEmpty()) {

            return "";
        }

        return value.substring(
                0,
                1
        ).toUpperCase()
                + value.substring(
                        1
                ).toLowerCase();
    }

    // =========================================================
    // STATUS
    // =========================================================

    private void setStatus(
            String text,
            String color) {

        if (statusLabel == null) {

            return;
        }

        statusLabel.setText(
                text
        );

        statusLabel.setStyle(
                "-fx-text-fill: "
                        + color
                        + ";"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private String getRootMessage(
            Throwable throwable) {

        if (throwable == null) {

            return "Unknown error.";
        }

        Throwable current =
                throwable;

        String message =
                throwable.getMessage();

        while (
                current.getCause() != null
        ) {

            current =
                    current.getCause();

            if (current.getMessage() != null
                    && !current.getMessage()
                            .trim()
                            .isEmpty()) {

                message =
                        current.getMessage();
            }
        }

        if (message == null
                || message.trim().isEmpty()) {

            return "An unexpected error occurred.";
        }

        return message;
    }

    // =========================================================
    // ALERT
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String message) {

        Alert alert =
                new Alert(
                        type
                );

        alert.setTitle(
                title
        );

        alert.setHeaderText(
                null
        );

        alert.setContentText(
                message
        );

        alert.showAndWait();
    }

    // =========================================================
    // DAY CONTROLS
    // =========================================================

    private static class DayControls {

        private final String dayName;

        private final CheckBox checkBox;

        private final ComboBox<String>
                startCombo;

        private final ComboBox<String>
                endCombo;

        private DayControls(
                String dayName,
                CheckBox checkBox,
                ComboBox<String> startCombo,
                ComboBox<String> endCombo) {

            this.dayName =
                    dayName;

            this.checkBox =
                    checkBox;

            this.startCombo =
                    startCombo;

            this.endCombo =
                    endCombo;
        }
    }
}