package com.healthsphere;

import com.healthsphere.view.Hospital.HospitalDashboardView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // Create the first screen of the Hospital module
        HospitalDashboardView dashboardView =
                new HospitalDashboardView();

        // Get the Dashboard scene
        Scene scene =
                dashboardView.createScene(stage);

        // Configure application window
        stage.setTitle(
                "Health-Sphere | Hospital Management System"
        );

        stage.setScene(scene);

        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        stage.setWidth(1280);
        stage.setHeight(820);

        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}