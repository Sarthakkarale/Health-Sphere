package com.healthsphere;

import com.healthsphere.view.Patient.PatientNavigator;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle(
                "MediNexus AI - Patient Module"
        );

        PatientNavigator navigator =
                new PatientNavigator(stage);

        navigator.showDashboard();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}