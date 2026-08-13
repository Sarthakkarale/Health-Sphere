package com.healthsphere;

import com.healthsphere.view.Patient.Dashboard;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Health-Sphere | Patient Portal");

        stage.setScene(
                new Dashboard(stage).getScene()
        );

        stage.setMinWidth(1200);
        stage.setMinHeight(750);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}