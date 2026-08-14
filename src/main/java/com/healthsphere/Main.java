package com.healthsphere;

import com.healthsphere.view.Patient.Dashboard;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Health-Sphere");

        // Direct navigation:
        // Main -> Dashboard
        Dashboard dashboard = new Dashboard(stage);

        stage.setScene(dashboard.getScene());
        stage.setWidth(1440);
        stage.setHeight(900);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}