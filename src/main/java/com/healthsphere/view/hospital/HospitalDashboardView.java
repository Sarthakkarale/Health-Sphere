package com.healthsphere.view.hospital;

import com.healthsphere.view.authentication.LoginView;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HospitalDashboardView {
    private final Stage stage;

    public HospitalDashboardView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 40;");

        Text title = new Text("Welcome to Hospital Dashboard");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-fill: #0256D0;");

        Button logoutBtn = new Button("Log Out");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginView(stage).getScene()));

        root.getChildren().addAll(title, logoutBtn);
        return new Scene(root, stage.getWidth(), stage.getHeight());
    }
}