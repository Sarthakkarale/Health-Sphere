package com.healthsphere.view.hospital;

import com.healthsphere.util.Navigation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Standardized Sidebar component for all Hospital Module views.
 */
public class HospitalSidebar {

    public enum HospitalTab {
        DASHBOARD("▦", "Dashboard"),
        DOCTORS("♙", "Doctors"),
        DEPARTMENTS("✚", "Departments"),
        BEDS("▥", "Beds"),
        APPOINTMENTS("▣", "Appointments"),
        PAYMENTS("💳", "Payment History"),
        REVIEWS("★", "Patient Reviews"),
        ANALYTICS("◈", "Analytics"),
        SETTINGS("⚙", "Hospital Settings");

        private final String icon;
        private final String title;

        HospitalTab(String icon, String title) {
            this.icon = icon;
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public String getTitle() {
            return title;
        }
    }

    // Modern Dark Sidebar Palette
    private static final String SIDEBAR_BG = "#0F172A";
    private static final String SIDEBAR_BORDER = "#1E293B";
    private static final String SIDEBAR_TEXT_MUTED = "#94A3B8";
    private static final String SIDEBAR_TEXT_HOVER = "#F8FAFC";
    private static final String SIDEBAR_HOVER_BG = "#1E293B";
    private static final String SIDEBAR_ACCENT = "#38BDF8";
    private static final String SIDEBAR_SELECTED = "#170ECA";

    private HospitalSidebar() {
        // Utility class
    }

    /**
     * Creates a uniform dark sidebar navigation bar for the Hospital module.
     *
     * @param stage The primary JavaFX Stage
     * @param activeTab The currently selected tab to highlight
     * @return VBox sidebar node
     */
    public static VBox createSidebar(Stage stage, HospitalTab activeTab) {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(240);
        sidebar.setPadding(new Insets(24, 16, 20, 16));

        sidebar.setStyle(
                "-fx-background-color: " + SIDEBAR_BG + ";" +
                "-fx-border-color: " + SIDEBAR_BORDER + ";" +
                "-fx-border-width: 0 1 0 0;"
        );

        // =====================================================
        // LOGO BRANDING
        // =====================================================
        VBox logoBox = new VBox(2);
        logoBox.setPadding(new Insets(0, 8, 24, 8));

        Label logo = new Label("Health-Sphere");
        logo.setStyle(
                "-fx-font-size: 22px;" +
                "-fx-font-weight: 800;" +
                "-fx-text-fill: " + SIDEBAR_ACCENT + ";"
        );

        Label subtitle = new Label("SMART HEALTHCARE");
        subtitle.setStyle(
                "-fx-font-size: 9px;" +
                "-fx-font-weight: bold;" +
                "-fx-letter-spacing: 1px;" +
                "-fx-text-fill: " + SIDEBAR_TEXT_MUTED + ";"
        );

        logoBox.getChildren().addAll(logo, subtitle);
        sidebar.getChildren().add(logoBox);

        // =====================================================
        // NAVIGATION BUTTONS
        // =====================================================
        for (HospitalTab tab : HospitalTab.values()) {
            boolean isSelected = (tab == activeTab);
            Button navBtn = createNavigationButton(tab.getIcon(), tab.getTitle(), isSelected);

            navBtn.setOnAction(e -> navigateToTab(stage, tab, activeTab));
            sidebar.getChildren().add(navBtn);
        }

        // =====================================================
        // SPACER
        // =====================================================
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        // =====================================================
        // FOOTER ACTIONS
        // =====================================================
        Button helpButton = createNavigationButton("?", "Help Center", false);
        helpButton.setOnAction(e -> showHelpCenterDialog());

        Button logoutButton = createNavigationButton("↪", "Logout", false);
        logoutButton.setOnAction(e -> Navigation.logout(stage));

        sidebar.getChildren().addAll(helpButton, logoutButton);

        return sidebar;
    }

    private static void navigateToTab(Stage stage, HospitalTab targetTab, HospitalTab currentTab) {
        if (targetTab == currentTab && stage != null) {
            // Already on this page
            return;
        }

        try {
            switch (targetTab) {
                case DASHBOARD:
                    stage.setScene(new HospitalDashboardView().createScene(stage));
                    break;
                case DOCTORS:
                    stage.setScene(new DoctorManagementView().createScene(stage));
                    break;
                case DEPARTMENTS:
                    stage.setScene(new DepartmentManagementView().createScene(stage));
                    break;
                case BEDS:
                    stage.setScene(new BedManagementView().createScene(stage));
                    break;
                case APPOINTMENTS:
                    stage.setScene(new AppointmentManagementView().createScene(stage));
                    break;
                case PAYMENTS:
                    stage.setScene(new HospitalPaymentsView().createScene(stage));
                    break;
                case REVIEWS:
                    stage.setScene(new HospitalReviewView().createScene(stage));
                    break;
                case ANALYTICS:
                    stage.setScene(new HospitalAnalyticsView().createScene(stage));
                    break;
                case SETTINGS:
                    stage.setScene(new HospitalProfileSettingsView().createScene(stage));
                    break;
            }
        } catch (Exception ex) {
            showErrorAlert("Navigation Error", "Could not open target screen: " + ex.getMessage());
        }
    }

    private static Button createNavigationButton(String icon, String text, boolean selected) {
        Button button = new Button();

        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
                "-fx-font-size: 16px;" +
                "-fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT_MUTED) + ";"
        );

        Label textLabel = new Label(text);
        textLabel.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: " + (selected ? "bold" : "500") + ";" +
                "-fx-text-fill: " + (selected ? "#FFFFFF" : SIDEBAR_TEXT_MUTED) + ";"
        );

        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().addAll(iconLabel, textLabel);

        button.setGraphic(content);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(0, 12, 0, 12));

        String baseStyle = "-fx-background-radius: 8; -fx-cursor: hand;";

        if (selected) {
            button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_SELECTED + ";");
        } else {
            button.setStyle(baseStyle + "-fx-background-color: transparent;");

            button.setOnMouseEntered(event -> {
                button.setStyle(baseStyle + "-fx-background-color: " + SIDEBAR_HOVER_BG + ";");
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SIDEBAR_TEXT_HOVER + ";");
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: " + SIDEBAR_TEXT_HOVER + ";");
            });

            button.setOnMouseExited(event -> {
                button.setStyle(baseStyle + "-fx-background-color: transparent;");
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + SIDEBAR_TEXT_MUTED + ";");
                textLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 500; -fx-text-fill: " + SIDEBAR_TEXT_MUTED + ";");
            });
        }

        return button;
    }

    private static void showHelpCenterDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help Center");
        alert.setHeaderText("Health-Sphere Hospital Support");
        alert.setContentText("For technical assistance or system support, please contact:\n\nEmail: support@healthsphere.com\nPhone: +1 (800) 555-HEALTH");
        alert.showAndWait();
    }

    private static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
