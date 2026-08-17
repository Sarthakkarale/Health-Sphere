package com.healthsphere;

import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.view.authentication.View;

public class Main {

    public static void main(String[] args) {

        try {

            System.out.println("Initializing Firebase...");

            FirebaseConfig.initialize();

            System.out.println(
                    "Firebase initialized successfully!"
            );

            Firestore db =
                    FirebaseConfig.getFirestore();

            Map<String, Object> testData =
                    new HashMap<>();

            testData.put(
                    "message",
                    "Health-Sphere Firebase Test"
            );

            testData.put(
                    "status",
                    "SUCCESS"
            );

            db.collection("connection_test")
                    .document("test")
                    .set(testData)
                    .get();

            System.out.println(
                    "Firestore WRITE successful!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Firebase/Firestore test failed!"
            );

            e.printStackTrace();

            return;
        }

        /*
         * View is the actual JavaFX Application.
         *
         * It owns the ONE shared Stage for the
         * entire HealthSphere application.
         */
        javafx.application.Application.launch(
                View.class,
                args
        );
    }
}