package com.healthsphere;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.view.authentication.View;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        try {
            System.out.println("Initializing Firebase...");
            FirebaseConfig.initialize();
            System.out.println("Firebase initialized successfully!");

        } catch (Exception e) {
            System.out.println("Firebase initialization failed!");
            e.printStackTrace();
            return;
        }

        View view = new View();
        view.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
