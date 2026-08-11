package com.healthsphere;

import com.healthsphere.view.admin.AdminMainShell;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        AdminMainShell adminShell = new AdminMainShell(primaryStage);
        adminShell.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}