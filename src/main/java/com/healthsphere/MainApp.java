package com.healthsphere;

import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.view.admin.AdminMainShell;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {
            // ============================================================
            // INITIALIZE FIREBASE BEFORE ANY DAO / CONTROLLER IS CREATED
            // ============================================================
            FirebaseConfig.initialize();

            System.out.println("Firebase initialized successfully.");

            // ============================================================
            // START ADMIN APPLICATION
            // ============================================================
            AdminMainShell adminShell =
                    new AdminMainShell(primaryStage);

            adminShell.show();

        } catch (Exception e) {

            System.err.println(
                    "Firebase initialization failed."
            );

            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("HealthSphere Startup Error");
            alert.setHeaderText("Unable to initialize HealthSphere.");
            alert.setContentText(
                    "Firebase could not be initialized.\n\n"
                    + "Please verify that:\n"
                    + "1. firebase/serviceAccountKey.json exists\n"
                    + "2. The Firebase credentials are valid\n"
                    + "3. The Firestore configuration is correct"
            );

            alert.showAndWait();

            // Close application because Firebase is required
            // for the current Admin module.
            primaryStage.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}