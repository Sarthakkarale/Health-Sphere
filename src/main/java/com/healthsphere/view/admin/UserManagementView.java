package com.healthsphere.view.admin;

import com.healthsphere.MainApp;
import com.healthsphere.view.admin.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene; // Import included
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class UserManagementView {

    private BorderPane mainLayout;
    private Stage stage;

    // Dynamic Mock Data for Live Search
    private final List<PatientRecord> patientData = new ArrayList<>();
    private VBox patientTableContainer;

    // Default Constructor
    public UserManagementView() {
        this(null);
    }

    // Constructor with Stage
    public UserManagementView(Stage stage) {
        this.stage = stage;
        initMockData();
    }

    private void initMockData() {
        patientData.add(new PatientRecord("USR-1001", "Prajwal Patil", "Patient", "prajwal@healthsphere.ai", "🟢 Active"));
        patientData.add(new PatientRecord("USR-1002", "Ananya Sharma", "Patient", "ananya.s@gmail.com", "🟢 Active"));
        patientData.add(new PatientRecord("USR-1003", "Vikram Singh", "Patient", "vikram.903@yahoo.com", "🔴 Suspended"));
        patientData.add(new PatientRecord("USR-1004", "Rohan Mehta", "Patient", "rohan.m@healthsphere.ai", "🟢 Active"));
        patientData.add(new PatientRecord("USR-1005", "Priya Kulkarni", "Patient", "priya.k@outlook.com", "🟢 Active"));
    }

    /**
     * Standardized view method returning the root Parent node.
     */
    public Parent getView() {
        return getContent();
    }

    /**
     * Standardized scene wrapper method.
     */
    public Scene getScene() {
        return createScene();
    }

    public Scene createScene() {
        Parent content = getContent();
        Stage targetStage = this.stage;
        double width = (targetStage != null && targetStage.getWidth() > 0) ? targetStage.getWidth() : 1366;
        double height = (targetStage != null && targetStage.getHeight() > 0) ? targetStage.getHeight() : 768;
        return new Scene(content, width, height);
    }

    public Parent getContent() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #F8FAFC;");

        // User / Patient List View rendered directly in Center
        mainLayout.setCenter(buildPatientDirectory());

        return mainLayout;
    }

    // ==========================================
    // USER / PATIENT DIRECTORY VIEW
    // ==========================================

    private ScrollPane buildPatientDirectory() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28, 32, 28, 32));

        HBox header = createHeader("User Directory Management", "Manage user profiles, account statuses, and system access.");

        VBox card = new VBox(16);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        HBox topBar = new HBox(12);
        topBar.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Registered Users (12,480 Total)");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("🔍 Search User ID, Name, Email...");
        search.setPrefWidth(260);
        search.setStyle("-fx-background-color: #F1F5F9; -fx-background-radius: 8px; -fx-padding: 8 12;");

        Button addPatientBtn = new Button("+ Register User");
        addPatientBtn.setStyle("-fx-background-color: #2563EB; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8px; -fx-padding: 8 14; -fx-cursor: hand;");
        addPatientBtn.setOnAction(e -> showInformationAlert("Register User", "Opening User Onboarding Wizard..."));

        topBar.getChildren().addAll(title, sp, search, addPatientBtn);

        patientTableContainer = new VBox(10);
        renderPatientRows(""); // Render all initially

        // Dynamic Search Handler
        search.textProperty().addListener((obs, oldVal, newVal) -> renderPatientRows(newVal));

        card.getChildren().addAll(topBar, patientTableContainer);
        root.getChildren().addAll(header, card);
        return wrapInScrollPane(root);
    }

    private void renderPatientRows(String filter) {
        patientTableContainer.getChildren().clear();
        String lowerFilter = filter.toLowerCase().trim();

        for (PatientRecord p : patientData) {
            if (lowerFilter.isEmpty() || p.id.toLowerCase().contains(lowerFilter) ||
                p.name.toLowerCase().contains(lowerFilter) || p.email.toLowerCase().contains(lowerFilter)) {
                
                patientTableContainer.getChildren().add(createPatientRow(p));
            }
        }

        if (patientTableContainer.getChildren().isEmpty()) {
            Label noMatch = new Label("⚠️ No matching user records found.");
            noMatch.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 13px; -fx-padding: 10;");
            patientTableContainer.getChildren().add(noMatch);
        }
    }

    private HBox createPatientRow(PatientRecord p) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12));
        row.setStyle("-fx-background-color: #F8FAFC; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        Label idLbl = new Label(p.id);
        idLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        idLbl.setTextFill(Color.web("#2563EB"));
        idLbl.setPrefWidth(90);

        Label nameLbl = new Label(p.name);
        nameLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        nameLbl.setPrefWidth(160);

        Label roleLbl = new Label(p.role);
        roleLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        roleLbl.setTextFill(Color.web("#64748B"));
        roleLbl.setPrefWidth(100);

        Label detailLbl = new Label(p.email);
        detailLbl.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 12));
        HBox.setHgrow(detailLbl, Priority.ALWAYS);

        Label statusLbl = new Label(p.status);
        statusLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        statusLbl.setPrefWidth(110);

        Button actionBtn = new Button(p.status.contains("Active") ? "Suspend" : "Activate");
        actionBtn.setStyle(p.status.contains("Active") 
            ? "-fx-background-color: #FEE2E2; -fx-text-fill: #DC2626; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;"
            : "-fx-background-color: #D1FAE5; -fx-text-fill: #059669; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-cursor: hand;");
        
        actionBtn.setOnAction(e -> {
            p.status = p.status.contains("Active") ? "🔴 Suspended" : "🟢 Active";
            renderPatientRows(""); // Refresh list
        });

        row.getChildren().addAll(idLbl, nameLbl, roleLbl, detailLbl, statusLbl, actionBtn);
        return row;
    }

    // ==========================================
    // UTILITY & UI HELPERS
    // ==========================================

    private HBox createHeader(String titleText, String subtitleText) {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Label title = new Label(titleText);
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#0F172A"));

        Label subtitle = new Label(subtitleText);
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        subtitle.setTextFill(Color.web("#64748B"));

        titleBox.getChildren().addAll(title, subtitle);
        header.getChildren().add(titleBox);
        return header;
    }

    private ScrollPane wrapInScrollPane(VBox content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #F8FAFC;");
        return scrollPane;
    }

    private void showInformationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(title);
        alert.setTitle("User Access Hub");
        alert.showAndWait();
    }

    // Helper Data Holder for Live Search Filter
    private static class PatientRecord {
        String id;
        String name;
        String role;
        String email;
        String status;

        PatientRecord(String id, String name, String role, String email, String status) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.email = email;
            this.status = status;
        }
    }
}