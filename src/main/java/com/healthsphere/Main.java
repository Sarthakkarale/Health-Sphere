package com.healthsphere;

import com.healthsphere.view.authentication.*;
import javafx.application.Application;

/**
 * Main Entry Point for Health-Sphere.
 * Delegates launch execution to your initial View application class.
 */
public class Main {

    public static void main(String[] args) {
        // Delegates execution directly to your initial View class which extends Application
        Application.launch(View.class, args);
    }
}

