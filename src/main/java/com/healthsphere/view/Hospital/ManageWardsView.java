package com.healthsphere.view.Hospital;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ManageWardsView {

    // Color Palette matching BedManagementView
    private static final String PRIMARY_BLUE = "#1E62D0";
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

        // NAVIGATE BACK TO BED MANAGEMENT
        backButton.setOnAction(e -> {
            BedManagementView bedView = new BedManagementView();
            stage.setScene(bedView.createScene(stage));
        });

        topHeader.getChildren().add(backButton);

        VBox titleBox = new VBox(4);
        Label title = new Label("Manage Wards");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: " + DARK_TEXT + ";");
        
        Label subtitle = new Label("View, add, and configure ward capacities and details.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        titleBox.getChildren().addAll(title, subtitle);

        // --- 2. MAIN CARD CONTENT ---
        VBox card = new VBox(18);
        card.setPadding(new Insets(24));
        applyCardStyle(card);

        Label cardTitle = new Label("Hospital Wards Overview");
        cardTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: " + DARK_TEXT + ";");

        // Ward Table / List Representation
        VBox wardList = new VBox(12);
        wardList.getChildren().addAll(
                createWardRow("General Ward", "240 Beds", "60 Available", "#059669"),
                createWardRow("ICU", "48 Beds", "12 Available", "#7C3AED"),
                createWardRow("Emergency Ward", "24 Beds", "18 Available", "#D97706"),
                createWardRow("Private Ward", "120 Beds", "36 Available", "#1E62D0")
        );

        card.getChildren().addAll(cardTitle, wardList);

        root.getChildren().addAll(topHeader, titleBox, card);

        return new Scene(root, stage.getWidth(), stage.getHeight());
    }

    private HBox createWardRow(String wardName, String capacity, String availability, String color) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle(
                "-fx-background-color: " + LIGHT_BACKGROUND + ";" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        VBox info = new VBox(2);
        Label name = new Label(wardName);
        name.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        Label cap = new Label("Total Capacity: " + capacity);
        cap.setStyle("-fx-font-size: 12px; -fx-text-fill: " + SECONDARY_TEXT + ";");
        info.getChildren().addAll(name, cap);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label avail = new Label(availability);
        avail.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Button editBtn = new Button("Edit Ward");
        editBtn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 6;" +
                "-fx-cursor: hand;"
        );

        row.getChildren().addAll(info, spacer, avail, editBtn);

        return row;
    }

    private void applyCardStyle(VBox card) {
        card.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.04));
        shadow.setRadius(10);
        shadow.setOffsetY(3);
        card.setEffect(shadow);
    }
}