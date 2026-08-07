package com.healthsphere;

import com.healthsphere.view.authentication.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Health-Sphere AI - Welcome & Account Ready");

        PatientRegistrationSuccessView successView = new PatientRegistrationSuccessView(primaryStage);
        primaryStage.setScene(successView.getScene());

        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(750);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}