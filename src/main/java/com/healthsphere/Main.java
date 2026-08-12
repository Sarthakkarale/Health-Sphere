package com.healthsphere;

import com.healthsphere.view.Hospital.HospitalDashboardView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // ---------------------------------------------------------
        // APPLICATION WINDOW
        // ---------------------------------------------------------

        stage.setTitle("Health-Sphere | Smart Healthcare");

        // Initial window size
        stage.setWidth(1400);
        stage.setHeight(850);

        // Minimum window size
        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        // ---------------------------------------------------------
        // INITIAL SCREEN
        // ---------------------------------------------------------

        HospitalDashboardView dashboardView =
                new HospitalDashboardView();

        Scene scene =
                dashboardView.createScene(stage);

        stage.setScene(scene);

        // ---------------------------------------------------------
        // WINDOW SETTINGS
        // ---------------------------------------------------------

        stage.setResizable(true);

        // Start maximized
        stage.setMaximized(true);

        stage.show();
    }

    // -------------------------------------------------------------
    // APPLICATION ENTRY POINT
    // -------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }
}

