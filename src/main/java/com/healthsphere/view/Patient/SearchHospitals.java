package com.healthsphere.view.Patient;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SearchHospitals {

    private final PatientNavigator navigator;

    public SearchHospitals(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    public Scene getScene() {

        VBox content = new VBox(18);

        VBox searchCard =
                PatientUI.card("Find a Hospital");

        TextField search =
                new TextField();

        search.setPromptText(
                "Search hospital, city or specialty..."
        );

        HBox.setHgrow(
                search,
                Priority.ALWAYS
        );

        Button searchButton =
                PatientUI.button(
                        "Search",
                        () -> {}
                );

        HBox searchRow =
                new HBox(10);

        searchRow.getChildren().addAll(
                search,
                searchButton
        );

        searchCard.getChildren().add(
                searchRow
        );

        VBox nearby =
                PatientUI.card(
                        "Recommended Hospitals"
                );

        nearby.getChildren().addAll(
                hospital(
                        "Apollo Hospitals",
                        "Jubilee Hills",
                        "Multi-Speciality",
                        "4.7 ★"
                ),

                hospital(
                        "Fortis Healthcare",
                        "Bengaluru",
                        "Multi-Speciality",
                        "4.6 ★"
                ),

                hospital(
                        "Max Healthcare",
                        "New Delhi",
                        "Multi-Speciality",
                        "4.5 ★"
                )
        );

        content.getChildren().addAll(
                searchCard,
                nearby
        );

        return PatientUI.createScene(
                navigator,
                "Search Hospitals",
                "Search Hospitals",
                "Find hospitals, doctors and healthcare facilities near you.",
                content
        );
    }

    private HBox hospital(
            String name,
            String location,
            String type,
            String rating
    ) {

        HBox row = new HBox(15);

        row.setPadding(
                new Insets(14)
        );

        VBox information =
                new VBox(5);

        Label nameLabel =
                new Label(name);

        nameLabel.setStyle(
                "-fx-font-size: 17px;" +
                "-fx-font-weight: bold;"
        );

        information.getChildren().addAll(
                nameLabel,
                PatientUI.muted(location),
                PatientUI.muted(type)
        );

        Button view =
                PatientUI.button(
                        "View Details",
                        () -> {}
                );

        row.getChildren().addAll(
                information,
                new Label(rating),
                view
        );

        HBox.setHgrow(
                information,
                Priority.ALWAYS
        );

        return row;
    }
}