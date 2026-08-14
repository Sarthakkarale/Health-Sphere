package com.healthsphere;

import com.healthsphere.view.doctor.DoctorDashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    // One common public static Stage
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) {

        primaryStage = stage;

        // Create Doctor Dashboard
        DoctorDashboardView dashboardView =
                new DoctorDashboardView(primaryStage);

        // Get dashboard scene
        Scene dashboardScene = dashboardView.getScene();

        // Set scene
        primaryStage.setScene(dashboardScene);

        // Window settings
        primaryStage.setTitle("Health-Sphere - Doctor Dashboard");
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        // Show application
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}