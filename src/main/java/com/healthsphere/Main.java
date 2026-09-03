package com.healthsphere;

<<<<<<<HEAD

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.view.authentication.View;

import javafx.application.Application;
import javafx.stage.Stage;=======
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.view.authentication.View;>>>>>>>origin/feature/patient

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        try {
<<<<<<< HEAD
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
    }=======

    System.out.println("Initializing Firebase...");

    FirebaseConfig.initialize();

    System.out.println("Firebase initialized successfully!");

    Firestore db = FirebaseConfig.getFirestore();

    Map<String, Object> testData = new HashMap<>();

    testData.put("message","Health-Sphere Firebase Test");

    testData.put("status","SUCCESS");

    db.collection("connection_test").document("test").set(testData).get();

    System.out.println("Firestore WRITE successful!");

    }catch(
    Exception e)
    {

        System.out.println(
                "Firebase/Firestore test failed!");

        e.printStackTrace();

        return;
    }

    /*
     * =====================================================
     * START JAVAFX APPLICATION
     * =====================================================
     *
     * View owns the ONE shared Stage.
     */
    javafx.application.Application.launch(View.class,args);
}>>>>>>>origin/feature/patient}