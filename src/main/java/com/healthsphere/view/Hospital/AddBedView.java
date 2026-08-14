

package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AddBedView {

    // Color Palette matching BedManagementView
    private static final String PRIMARY_BLUE = "#170eca";
    private static final String DARK_TEXT = "#0F172A";
    private static final String SECONDARY_TEXT = "#64748B";
    private static final String LIGHT_BACKGROUND = "#F8FAFC";
    private static final String CARD_BG = "#FFFFFF";
    private static final String BORDER = "#E2E8F0";

    public Scene createScene(Stage stage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + ";");

        // --- 1. HEADER & BACK BUTTON ---
        HBox topHeader = new HBox(12);
        topHeader.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back to Bed Management");
        backButton.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY_BLUE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;"
        );

        // NAVIGATION BACK TO BED MANAGEMENT
        backButton.setOnAction(e -> {
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
        });

        topHeader.getChildren().add(backButton);

        VBox titleBox = new VBox(4);
        Label title = new Label("Add New Bed");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");
        
        Label subtitle = new Label("Register a new bed entry into the hospital inventory system.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        // --- 2. FORM CARD ---
        VBox formCard = new VBox(18);
        formCard.setPadding(new Insets(24));
        formCard.setMaxWidth(600);
        formCard.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.04));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        formCard.setEffect(shadow);

        // Input: Bed Number
        VBox bedNumBox = createFormField("Bed Number / ID", new TextField(), "e.g. ICU-104 or GW-212");

        // Input: Ward Selection
        ComboBox<String> wardCombo = new ComboBox<>();
        wardCombo.getItems().addAll("General Ward", "ICU", "Emergency", "Private Ward");
        wardCombo.setPromptText("Select Ward");
        wardCombo.setMaxWidth(Double.MAX_VALUE);
        VBox wardBox = createFormField("Assign Ward", wardCombo, null);

        // Input: Bed Type
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Standard", "ICU / Ventilator", "Bariatric", "Pediatric");
        typeCombo.setPromptText("Select Type");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        VBox typeBox = createFormField("Bed Type", typeCombo, null);

        // Input: Initial Status
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Available", "Occupied", "Maintenance", "Reserved");
        statusCombo.setValue("Available");
        statusCombo.setMaxWidth(Double.MAX_VALUE);
        VBox statusBox = createFormField("Initial Status", statusCombo, null);

        // Action Buttons
        HBox formActions = new HBox(12);
        formActions.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 8 16;" +
            "-fx-cursor: hand;"
        );
        cancelBtn.setOnAction(e -> {
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
        });

        Button submitBtn = new Button("Save Bed");
        submitBtn.setStyle(
            "-fx-background-color: " + PRIMARY_BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 8 20;" +
            "-fx-cursor: hand;"
        );
        
        // Save logic & Navigate Back
        submitBtn.setOnAction(e -> {
            // TODO: Add database/API save logic here
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
        });

        formActions.getChildren().addAll(cancelBtn, submitBtn);

        formCard.getChildren().addAll(bedNumBox, wardBox, typeBox, statusBox, formActions);

        root.getChildren().addAll(topHeader, titleBox, formCard);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private VBox createFormField(String labelText, Control inputControl, String prompt) {
        VBox box = new VBox(6);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: " + DARK_TEXT + ";");

        if (inputControl instanceof TextField && prompt != null) {
            ((TextField) inputControl).setPromptText(prompt);
        }

        inputControl.setStyle("-fx-background-color: " + LIGHT_BACKGROUND + "; -fx-border-color: " + BORDER + "; -fx-border-radius: 6; -fx-padding: 6 10;");

        box.getChildren().addAll(label, inputControl);
        return box;
    }
}