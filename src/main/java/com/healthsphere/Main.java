package com.healthsphere;

import com.healthsphere.view.authentication.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Health-Sphere | Select Portal");

            SelectAccountView selectAccountView = new SelectAccountView(primaryStage);
            Scene scene = selectAccountView.getScene();

            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1024);
            primaryStage.setMinHeight(720);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}