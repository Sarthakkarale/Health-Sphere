package com.healthsphere;

import com.healthsphere.view.doctor.DoctorDashboardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Health-Sphere Portal");

        primaryStage.setWidth(1380);
        primaryStage.setHeight(900);
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(700);

        DoctorDashboardView dashboardView = new DoctorDashboardView(primaryStage);
        Scene initialScene = dashboardView.createScene();

        primaryStage.setScene(initialScene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}