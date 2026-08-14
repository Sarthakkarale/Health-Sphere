// package com.healthsphere;

// import com.healthsphere.view.authentication.*;
// import javafx.application.Application;

// /**
//  * Main Entry Point for Health-Sphere.
//  * Delegates launch execution to your initial View application class.
//  */
// public class Main {

//     public static void main(String[] args) {
//         // Delegates execution directly to your initial View class which extends Application
//         Application.launch(View.class, args);
//     }
// }

package com.healthsphere;

import com.healthsphere.view.authentication.SplashView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        // Create Splash Screen
        SplashView splashView = new SplashView();

        // Create Scene
        Scene scene = splashView.getScene();

        // Configure Stage
        primaryStage.setTitle("Health-Sphere");
        primaryStage.setScene(scene);

        primaryStage.setWidth(1440);
        primaryStage.setHeight(900);

        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    public static void main(String[] args) {

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

    Application.launch(View.class, args);
}
}

