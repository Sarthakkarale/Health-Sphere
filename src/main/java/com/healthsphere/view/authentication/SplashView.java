package com.healthsphere.view.authentication;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class SplashView {

    private Scene scene;

    public SplashView() {

        // Application Name
        Text title = new Text("Health-Sphere");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 42));
        title.setFill(Color.web("#2563EB"));

        // Tagline
        Text tagline = new Text("AI Powered Healthcare Platform");
        tagline.setFont(Font.font("Arial", 20));
        tagline.setFill(Color.GRAY);

        // Loading Indicator
        ProgressIndicator loading = new ProgressIndicator();
        loading.setPrefSize(70, 70);

        // Layout
        VBox root = new VBox(25);

        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                title,
                tagline,
                loading
        );

        root.setStyle("""
                -fx-background-color: white;
                """);

        scene = new Scene(root, 1440, 900);
    }

    public Scene getScene() {
        return scene;
    }
}