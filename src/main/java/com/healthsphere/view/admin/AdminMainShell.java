package com.healthsphere.view.admin;

import com.healthsphere.util.UIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class AdminMainShell {

    private final Stage stage;
    private BorderPane mainLayout;
    private final List<Button> navButtons = new ArrayList<>();
    private int currentActiveIndex = 0;

    public AdminMainShell(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + UIUtils.COLOR_BG_DARK + ";");

        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        switchView(0, new AdminDashboardView(stage).getView());

        double w = (stage != null && stage.getWidth() > 0) ? stage.getWidth() : 1366;
        double h = (stage != null && stage.getHeight() > 0) ? stage.getHeight() : 768;

        return new Scene(mainLayout, w, h);
    }

    public void show() {
        if (stage != null) {
            stage.setTitle("HealthSphere AI — Enterprise Command Center");
            stage.setScene(getScene());
            stage.setMaximized(true);
            stage.show();
        }
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(14);
        sidebar.setPrefWidth(270);
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.setStyle("-fx-background-color: " + UIUtils.COLOR_SIDEBAR + "; -fx-border-color: #1E293B; -fx-border-width: 0 1 0 0;");

        // Logo
        HBox logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 12, 8));

        StackPane iconPane = new StackPane();
        Circle iconBg = new Circle(18, Color.web("#4F46E5"));
        iconBg.setEffect(UIUtils.getGlowEffect("#6366F1", 12));
        Label logoIcon = new Label("✨");
        logoIcon.setFont(Font.font(16));
        iconPane.getChildren().addAll(iconBg, logoIcon);

        VBox titleBox = new VBox(2);
        Label logoText = new Label("HealthSphere");
        logoText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        logoText.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        Label subText = new Label("COMMAND CENTER v4.2");
        subText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
        subText.setTextFill(Color.web("#818CF8"));

        titleBox.getChildren().addAll(logoText, subText);
        logoBox.getChildren().addAll(iconPane, titleBox);

        // Navigation
        VBox navList = new VBox(6);
        navButtons.clear();

        navList.getChildren().addAll(
            createNavButton("📊  Command Dashboard", 0, () -> switchView(0, new AdminDashboardView(stage).getView())),
            createNavButton("👥  User Directory", 1, () -> switchView(1, new UserManagementView(stage).getView())),
            createNavButton("🏥  Hospital Verification", 2, () -> switchView(2, new HospitalManagementView(stage).getView())),
            createNavButton("🩺  Doctor Credentialing", 3, () -> switchView(3, new DoctorManagementView(stage).getView())),
            createNavButton("🚨  Reports & Moderation", 4, () -> switchView(4, new ComplaintsManagementView(stage).getView())),
            createNavButton("📈  BI & Analytics", 5, () -> switchView(5, new ReportsAnalyticsView(stage).getView())),
            createNavButton("⚙️  Neural Settings", 6, () -> switchView(6, new AdminSettingsView(stage).getView()))
        );

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox footerCard = createFooterUserCard();
        sidebar.getChildren().addAll(logoBox, navList, spacer, footerCard);
        return sidebar;
    }

    private Button createNavButton(String text, int index, Runnable onClick) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(12, 16, 12, 16));

        applyInactiveStyle(btn);

        btn.setOnMouseEntered(e -> {
            if (navButtons.indexOf(btn) != currentActiveIndex) {
                btn.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #E2E8F0; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 10; -fx-cursor: hand;");
            }
        });

        btn.setOnMouseExited(e -> {
            if (navButtons.indexOf(btn) != currentActiveIndex) {
                applyInactiveStyle(btn);
            }
        });

        btn.setOnAction(e -> onClick.run());
        navButtons.add(btn);
        return btn;
    }

    private void switchView(int selectedIndex, Node viewNode) {
        currentActiveIndex = selectedIndex;
        mainLayout.setCenter(viewNode);
        for (int i = 0; i < navButtons.size(); i++) {
            Button btn = navButtons.get(i);
            if (i == selectedIndex) applyActiveStyle(btn);
            else applyInactiveStyle(btn);
        }
    }

    private void applyActiveStyle(Button btn) {
        btn.setStyle("-fx-background-color: linear-gradient(to right, #312E81, #4338CA); -fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 10; -fx-border-color: #6366F1; -fx-border-radius: 10; -fx-cursor: hand;");
        btn.setEffect(UIUtils.getGlowEffect("#6366F1", 10));
    }

    private void applyInactiveStyle(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-font-size: 13px; -fx-border-color: transparent; -fx-cursor: hand;");
        btn.setEffect(null);
    }

    private VBox createFooterUserCard() {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + UIUtils.COLOR_CARD_DARK + "; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;");

        HBox userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_LEFT);

        Circle avatar = new Circle(14, Color.web(UIUtils.COLOR_ACCENT));
        
        VBox info = new VBox(2);
        Label name = new Label("Prajwal Patil");
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        name.setTextFill(Color.web(UIUtils.COLOR_TEXT_MAIN));

        Label role = new Label("Super Administrator");
        role.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 10));
        role.setTextFill(Color.web(UIUtils.COLOR_TEXT_MUTED));

        info.getChildren().addAll(name, role);
        userBox.getChildren().addAll(avatar, info);

        HBox statusBox = new HBox(6);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Circle liveDot = new Circle(4, Color.web(UIUtils.COLOR_SUCCESS));
        liveDot.setEffect(UIUtils.getGlowEffect(UIUtils.COLOR_SUCCESS, 6));

        Label statusText = new Label("Node: Asia-South1 (Active)");
        statusText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 10));
        statusText.setTextFill(Color.web(UIUtils.COLOR_SUCCESS));
        statusBox.getChildren().addAll(liveDot, statusText);

        card.getChildren().addAll(userBox, statusBox);
        return card;
    }
}