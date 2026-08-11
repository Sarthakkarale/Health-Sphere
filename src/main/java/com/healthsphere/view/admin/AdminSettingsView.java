package com.healthsphere.view.admin;

import com.healthsphere.util.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class AdminSettingsView {

    private final Stage stage;

    public AdminSettingsView(Stage stage) {
        this.stage = stage;
    }

    public Node getView() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: " + UIUtils.COLOR_BG_DARK + ";");

        VBox titleBox = new VBox(4);
        Label title = new Label("System Settings & Neural Configurations");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        Label subTitle = new Label("Global system thresholds, security enforcement, and API endpoint configurations.");
        subTitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        subTitle.setTextFill(Color.web(UIUtils.COLOR_TEXT_MUTED));
        titleBox.getChildren().addAll(title, subTitle);

        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + UIUtils.COLOR_CARD_DARK + "; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;");

        CheckBox cb1 = new CheckBox("Enable Automated AI Medical Verification Pre-screening");
        cb1.setSelected(true);
        cb1.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        CheckBox cb2 = new CheckBox("Require Multi-Factor Authentication (MFA) for Super Admins");
        cb2.setSelected(true);
        cb2.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        HBox apiBox = new HBox(12);
        apiBox.setAlignment(Pos.CENTER_LEFT);
        Label apiLabel = new Label("AI Engine Endpoint:");
        apiLabel.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));
        
        TextField apiField = new TextField("https://api.healthsphere.ai/v1/diagnose");
        apiField.setPrefWidth(320);
        apiField.setStyle("-fx-background-color: " + UIUtils.COLOR_BG_DARK + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 10; -fx-border-color: #334155; -fx-border-radius: 6;");
        apiBox.getChildren().addAll(apiLabel, apiField);

        Button saveBtn = new Button("Save Configuration Changes");
        saveBtn.setStyle("-fx-background-color: #6366F1; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 9 16; -fx-background-radius: 8; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> UIUtils.showInfoDialog("Settings Saved", "System configuration updated successfully."));

        card.getChildren().addAll(cb1, cb2, apiBox, saveBtn);
        root.getChildren().addAll(titleBox, card);

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: " + UIUtils.COLOR_BG_DARK + "; -fx-border-color: transparent;");
        return scroll;
    }
}