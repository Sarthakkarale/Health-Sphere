package com.healthsphere;

import com.healthsphere.view.authentication.SplashView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Create Splash Screen
        SplashView splashView = new SplashView();

        // Create Scene
        Scene scene = splashView.getScene();

        // Configure Stage
        primaryStage.setTitle("Health-Sphere");
        primaryStage.setScene(scene);

        primaryStage.setWidth(1440);
        primaryStage.setHeight(900);

        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}