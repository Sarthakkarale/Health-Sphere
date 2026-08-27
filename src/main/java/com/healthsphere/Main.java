package com.healthsphere;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.Firestore;
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

            Firestore db = FirebaseConfig.getFirestore();

            Map<String, Object> testData = new HashMap<>();

            testData.put("message", "Health-Sphere Firebase Test");
            testData.put("status", "SUCCESS");

            db.collection("connection_test")
                    .document("test")
                    .set(testData)
                    .get();

            System.out.println("Firestore WRITE successful!");

        } catch (Exception e) {

            System.out.println("Firebase/Firestore test failed!");
            e.printStackTrace();

            return;
        }

        /*
         * Start the authentication View.
         *
         * IMPORTANT:
         * Do not use:
         *
         * Application.launch(View.class, stage);
         *
         * because launch() accepts String arguments, not Stage.
         */

        View view = new View();

        /*
         * If View contains its own JavaFX UI initialization,
         * call the appropriate method from View here.
         *
         * For example:
         *
         * view.start(stage);
         */

        view.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}