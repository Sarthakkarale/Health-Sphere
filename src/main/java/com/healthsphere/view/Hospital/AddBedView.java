package com.healthsphere.view.Hospital;

import com.healthsphere.controller.hospital.BedController;
import com.healthsphere.controller.hospital.WardController;
import com.healthsphere.model.HospitalWard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class AddBedView {

    // =========================================================
    // COLOR PALETTE
    // =========================================================

    private static final String PRIMARY_BLUE = "#170eca";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    // =========================================================
    // CONTROLLERS
    // =========================================================

    private final BedController bedController;
    private final WardController wardController;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public AddBedView() {
        this.bedController = new BedController();
        this.wardController = new WardController();
    }

    // =========================================================
    // CREATE SCENE
    // =========================================================

    public Scene createScene(Stage stage) {

        VBox root = new VBox(20);

        root.setPadding(new Insets(32));

        root.setStyle(
                "-fx-background-color: " +
                LIGHT_BACKGROUND +
                ";"
        );

        // =====================================================
        // HEADER
        // =====================================================

        HBox topHeader = new HBox(12);

        topHeader.setAlignment(Pos.CENTER_LEFT);

        Button backButton =
                new Button("← Back to Bed Management");

        backButton.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: " + PRIMARY_BLUE + ";" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 13px;" +
                "-fx-cursor: hand;"
        );

        backButton.setOnAction(e -> {

            BedManagementView bedView =
                    new BedManagementView();

            stage.setScene(
                    bedView.createScene(stage)
            );
        });

        topHeader.getChildren().add(backButton);

        VBox titleBox = new VBox(4);

        Label title =
                new Label("Add New Bed");

        title.setStyle(
                "-fx-font-size: 24px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " +
                DARK_TEXT +
                ";"
        );

        Label subtitle =
                new Label(
                        "Register a new bed entry into the " +
                        "hospital inventory system."
                );

        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: " +
                SECONDARY_TEXT +
                ";"
        );

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        // =====================================================
        // FORM CARD
        // =====================================================

        VBox formCard = new VBox(18);

        formCard.setPadding(new Insets(24));

        formCard.setMaxWidth(600);

        formCard.setStyle(
                "-fx-background-color: " +
                CARD_BG +
                ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " +
                BORDER +
                ";" +
                "-fx-border-radius: 12;"
        );

        DropShadow shadow =
                new DropShadow();

        shadow.setColor(
                Color.rgb(15, 23, 42, 0.04)
        );

        shadow.setRadius(10);

        shadow.setOffsetY(3);

        formCard.setEffect(shadow);

        // =====================================================
        // BED NUMBER
        // =====================================================

        TextField bedNumberField =
                new TextField();

        VBox bedNumBox =
                createFormField(
                        "Bed Number / ID",
                        bedNumberField,
                        "e.g. ICU-104 or GW-212"
                );

        // =====================================================
        // WARD
        // =====================================================

        ComboBox<HospitalWard> wardCombo =
                new ComboBox<>();

        wardCombo.setPromptText(
                "Select Ward"
        );

        wardCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        /*
         * Display only the ward name in the ComboBox.
         * The actual HospitalWard object contains the wardId.
         */
        wardCombo.setConverter(
                new javafx.util.StringConverter<HospitalWard>() {

                    @Override
                    public String toString(
                            HospitalWard ward) {

                        if (ward == null) {
                            return "";
                        }

                        return ward.getName();
                    }

                    @Override
                    public HospitalWard fromString(
                            String string) {

                        return null;
                    }
                }
        );

        VBox wardBox =
                createFormField(
                        "Assign Ward",
                        wardCombo,
                        null
                );

        // =====================================================
        // BED TYPE
        // =====================================================

        ComboBox<String> typeCombo =
                new ComboBox<>();

        typeCombo.getItems().addAll(
                "Standard",
                "ICU / Ventilator",
                "Bariatric",
                "Pediatric"
        );

        typeCombo.setPromptText(
                "Select Type"
        );

        typeCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox typeBox =
                createFormField(
                        "Bed Type",
                        typeCombo,
                        null
                );

        // =====================================================
        // INITIAL STATUS
        // =====================================================

        ComboBox<String> statusCombo =
                new ComboBox<>();

        statusCombo.getItems().addAll(
                "Available",
                "Occupied",
                "Maintenance",
                "Reserved"
        );

        statusCombo.setValue(
                "Available"
        );

        statusCombo.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox statusBox =
                createFormField(
                        "Initial Status",
                        statusCombo,
                        null
                );

        // =====================================================
        // LOAD WARDS
        // =====================================================

        loadWards(
                wardCombo
        );

        // =====================================================
        // FORM ACTIONS
        // =====================================================

        HBox formActions =
                new HBox(12);

        formActions.setAlignment(
                Pos.CENTER_RIGHT
        );

        // -----------------------------------------------------
        // CANCEL
        // -----------------------------------------------------

        Button cancelBtn =
                new Button("Cancel");

        cancelBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-padding: 8 16;" +
                "-fx-cursor: hand;"
        );

        cancelBtn.setOnAction(e -> {

            BedManagementView bedView =
                    new BedManagementView();

            stage.setScene(
                    bedView.createScene(stage)
            );
        });

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Button submitBtn =
                new Button("Save Bed");

        submitBtn.setStyle(
                "-fx-background-color: " +
                PRIMARY_BLUE +
                ";" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 8 20;" +
                "-fx-cursor: hand;"
        );

        submitBtn.setOnAction(e -> {

            handleSaveBed(
                    stage,
                    bedNumberField,
                    wardCombo,
                    typeCombo,
                    statusCombo
            );
        });

        formActions.getChildren().addAll(
                cancelBtn,
                submitBtn
        );

        // =====================================================
        // ADD FORM COMPONENTS
        // =====================================================

        formCard.getChildren().addAll(
                bedNumBox,
                wardBox,
                typeBox,
                statusBox,
                formActions
        );

        root.getChildren().addAll(
                topHeader,
                titleBox,
                formCard
        );

        return new Scene(
                root,
                stage.getWidth(),
                stage.getHeight()
        );
    }

    // =========================================================
    // LOAD WARDS FROM FIRESTORE
    // =========================================================

    private void loadWards(
            ComboBox<HospitalWard> wardCombo) {

        try {

            List<HospitalWard> wards =
                    wardController.getAllWards();

            wardCombo.getItems().clear();

            wardCombo.getItems().addAll(
                    wards
            );

            if (wards.isEmpty()) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "No Wards Available",
                        "No active hospital wards were found.",
                        "Please create a ward before adding a bed."
                );
            }

        } catch (Exception ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Unable to Load Wards",
                    "The hospital wards could not be loaded.",
                    getErrorMessage(ex)
            );
        }
    }

    // =========================================================
    // SAVE BED
    // =========================================================

    private void handleSaveBed(
            Stage stage,
            TextField bedNumberField,
            ComboBox<HospitalWard> wardCombo,
            ComboBox<String> typeCombo,
            ComboBox<String> statusCombo) {

        String bedNumber =
                bedNumberField.getText();

        HospitalWard selectedWard =
                wardCombo.getValue();

        String bedType =
                typeCombo.getValue();

        String status =
                statusCombo.getValue();

        // -----------------------------------------------------
        // BASIC UI VALIDATION
        // -----------------------------------------------------

        if (bedNumber == null ||
                bedNumber.trim().isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Bed number is required.",
                    "Please enter a bed number such as ICU-104."
            );

            bedNumberField.requestFocus();

            return;
        }

        if (selectedWard == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Ward selection is required.",
                    "Please select the ward to which this bed belongs."
            );

            wardCombo.requestFocus();

            return;
        }

        if (bedType == null ||
                bedType.trim().isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Bed type is required.",
                    "Please select a bed type."
            );

            typeCombo.requestFocus();

            return;
        }

        if (status == null ||
                status.trim().isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Bed status is required.",
                    "Please select the initial bed status."
            );

            statusCombo.requestFocus();

            return;
        }

        // -----------------------------------------------------
        // BUSINESS VALIDATION
        // -----------------------------------------------------

        try {

            /*
             * A newly created occupied bed would require a
             * patient ID. Since this form does not select a
             * patient, we don't allow that here.
             */
            if (status.equalsIgnoreCase("Occupied")) {

                showAlert(
                        Alert.AlertType.WARNING,
                        "Invalid Initial Status",
                        "A new bed cannot be marked Occupied here.",
                        "Create the bed as Available or Reserved. " +
                        "A patient can be assigned later."
                );

                return;
            }

            // -------------------------------------------------
            // SAVE THROUGH CONTROLLER
            // -------------------------------------------------

            String bedId =
                    bedController.createBed(
                            selectedWard.getWardId(),
                            bedNumber.trim(),
                            bedType,
                            status
                    );

            // -------------------------------------------------
            // SUCCESS
            // -------------------------------------------------

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Bed Added",
                    "Bed has been successfully registered.",
                    "Bed ID: " + bedId +
                    "\nBed Number: " + bedNumber.trim() +
                    "\nWard: " + selectedWard.getName()
            );

            // -------------------------------------------------
            // NAVIGATE BACK
            // -------------------------------------------------

            BedManagementView bedView =
                    new BedManagementView();

            stage.setScene(
                    bedView.createScene(stage)
            );

        } catch (IllegalArgumentException ex) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Unable to Save Bed",
                    "The bed could not be registered.",
                    getErrorMessage(ex)
            );

        } catch (Exception ex) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Database Error",
                    "An unexpected error occurred while saving the bed.",
                    getErrorMessage(ex)
            );
        }
    }

    // =========================================================
    // FORM FIELD HELPER
    // =========================================================

    private VBox createFormField(
            String labelText,
            Control inputControl,
            String prompt) {

        VBox box =
                new VBox(6);

        Label label =
                new Label(labelText);

        label.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: 600;" +
                "-fx-text-fill: " +
                DARK_TEXT +
                ";"
        );

        if (inputControl instanceof TextField &&
                prompt != null) {

            ((TextField) inputControl)
                    .setPromptText(prompt);
        }

        inputControl.setStyle(
                "-fx-background-color: " +
                LIGHT_BACKGROUND +
                ";" +
                "-fx-border-color: " +
                BORDER +
                ";" +
                "-fx-border-radius: 6;" +
                "-fx-padding: 6 10;"
        );

        box.getChildren().addAll(
                label,
                inputControl
        );

        return box;
    }

    // =========================================================
    // ALERT HELPER
    // =========================================================

    private void showAlert(
            Alert.AlertType type,
            String title,
            String header,
            String content) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    // =========================================================
    // ERROR MESSAGE HELPER
    // =========================================================

    private String getErrorMessage(
            Exception exception) {

        if (exception.getMessage() != null &&
                !exception.getMessage().trim().isEmpty()) {

            return exception.getMessage();
        }

        if (exception.getCause() != null &&
                exception.getCause().getMessage() != null) {

            return exception.getCause().getMessage();
        }

        return "Please try again.";
    }
}