package com.healthsphere;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.view.authentication.View;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {

        try {

            System.out.println("Initializing Firebase...");

            FirebaseConfig.initialize();

            System.out.println(
                    "Firebase initialized successfully!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Firebase initialization failed!"
            );

            e.printStackTrace();

            return;
        }

        Application.launch(
                View.class,
                args
        );
    }
}