package com.healthsphere.view.doctor;

import com.healthsphere.util.Navigation;
import com.healthsphere.util.SessionManager;
import com.healthsphere.view.authentication.LoginView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;

/**
 * DoctorSidebar
 * 
 * Unified common sidebar component for all Doctor views in Health-Sphere.
 */
public class DoctorSidebar {

    private DoctorSidebar() {}

    public static VBox create(Stage stage, int activeIndex) {
        return create(stage, activeIndex, null);
    }

    public static VBox create(Stage stage, int activeIndex, String selectedPatientUid) {
        VBox sidebar = new VBox();
        sidebar.setPadding(new Insets(24, 16, 24, 16));
        sidebar.setMinWidth(260);
        sidebar.setPrefWidth(260);
        sidebar.setMaxWidth(260);
        sidebar.setStyle("-fx-background-color: #12355B;");

        // Logo section
        HBox logoSection = new HBox(12);
        logoSection.setAlignment(Pos.CENTER_LEFT);
        logoSection.setPadding(new Insets(0, 0, 28, 4));

        StackPane logoBox = new StackPane();
        logoBox.setStyle("-fx-background-color: #2F80ED; -fx-background-radius: 8px; -fx-padding: 8px;");
        ImageView logo = createImageView("/images/icons/ic_shield.png", 20, 20);
        if (logo != null) logoBox.getChildren().add(logo);

        VBox logoText = new VBox(2);
        Label appName = new Label("Health-Sphere");
        appName.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label doctorSubtext = new Label("Doctor Dashboard");
        doctorSubtext.setStyle("-fx-text-fill: #D6E4F0; -fx-font-size: 12px;");
        logoText.getChildren().addAll(appName, doctorSubtext);
        logoSection.getChildren().addAll(logoBox, logoText);

        // Sidebar Tabs Navigation
        VBox navItems = new VBox(6);
        String[] tabs = {
                "Dashboard",
                "Today's Schedule",
                "Appointments",
                "Sessions",
                "Patient Details",
                "Medical Reports & Prescription",
                "Availability & Schedule",
                "Doctor Profile",
                "Payment History"
        };

        String[] icons = {
                "ic_dashboard.png",
                "ic_schedule.png",
                "ic_appointments.png",
                "ic_video.png",
                "ic_patient.png",
                "ic_reports.png",
                "ic_availability.png",
                "ic_profile.png",
                "ic_card_white.png"
        };

        for (int i = 0; i < tabs.length; i++) {
            final int tabIndex = i;
            HBox navTab = new HBox(12);
            navTab.setAlignment(Pos.CENTER_LEFT);
            navTab.setPadding(new Insets(10, 14, 10, 14));

            ImageView icon = createImageView("/images/icons/" + icons[i], 18, 18);
            Label tabLabel = new Label(tabs[i]);
            tabLabel.setWrapText(true);
            HBox.setHgrow(tabLabel, Priority.ALWAYS);

            if (i == activeIndex) {
                navTab.setStyle("-fx-background-color: #2F80ED; -fx-background-radius: 8px; -fx-cursor: hand;");
                tabLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
            } else {
                navTab.setStyle("-fx-background-color: transparent; -fx-background-radius: 8px; -fx-cursor: hand;");
                tabLabel.setStyle("-fx-text-fill: #D6E4F0; -fx-font-size: 13px;");
            }

            if (icon != null) navTab.getChildren().add(icon);
            navTab.getChildren().add(tabLabel);

            navTab.setOnMouseClicked(e -> navigateTo(stage, tabIndex, selectedPatientUid));
            navItems.getChildren().add(navTab);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Doctor Profile Box
        HBox profile = new HBox(12);
        profile.setAlignment(Pos.CENTER_LEFT);
        profile.setPadding(new Insets(10, 12, 10, 12));
        profile.setStyle("-fx-background-color: #1D4E7A; -fx-background-radius: 10px; -fx-cursor: hand;");

        ImageView profileImage = createImageView("/images/doctor/doctor_profile.png", 34, 34);
        if (profileImage == null) {
            profileImage = createImageView("/images/doctor/portrait-3d-male-doctor.png", 34, 34);
        }
        VBox profileText = new VBox(2);
        Label doctorRole = new Label("Doctor Profile");
        doctorRole.setStyle("-fx-text-fill: #D6E4F0; -fx-font-size: 11px;");
        Label doctorName = new Label(SessionManager.getDoctorDisplayName());
        doctorName.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        profileText.getChildren().addAll(doctorRole, doctorName);

        if (profileImage != null) profile.getChildren().add(profileImage);
        profile.getChildren().add(profileText);
        profile.setOnMouseClicked(e -> navigateTo(stage, 7, selectedPatientUid));

        // Logout Box
        HBox logout = new HBox(12);
        logout.setAlignment(Pos.CENTER_LEFT);
        logout.setPadding(new Insets(10, 14, 10, 14));
        logout.setStyle("-fx-cursor: hand;");
        ImageView logoutIcon = createImageView("/images/icons/ic_logout.png", 18, 18);
        Label logoutLabel = new Label("Logout");
        logoutLabel.setStyle("-fx-text-fill: #D6E4F0; -fx-font-size: 14px;");
        if (logoutIcon != null) logout.getChildren().add(logoutIcon);
        logout.getChildren().add(logoutLabel);

        logout.setOnMouseClicked(e -> {
            try {
                SessionManager.getInstance().clearSession();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Navigation.goTo(stage, () -> new LoginView(stage).getScene());
        });

        sidebar.getChildren().addAll(logoSection, navItems, spacer, profile, logout);
        return sidebar;
    }

    private static void navigateTo(Stage stage, int index, String selectedPatientUid) {
        switch (index) {
            case 0:
                Navigation.goTo(stage, () -> new DoctorDashboardView(stage).getScene());
                break;
            case 1:
                Navigation.goTo(stage, () -> new TodaysScheduleView(stage).getScene());
                break;
            case 2:
                Navigation.goTo(stage, () -> new AppointmentsView(stage).getScene());
                break;
            case 3:
                Navigation.goTo(stage, () -> new DoctorSessionsView(stage).getScene());
                break;
            case 4:
                Navigation.goTo(stage, () -> new PatientDetailsView(stage).getScene());
                break;
            case 5:
                if (selectedPatientUid != null && !selectedPatientUid.isBlank()) {
                    Navigation.goTo(stage, () -> new MedicalReportsView(stage, selectedPatientUid).getScene());
                } else {
                    Navigation.goTo(stage, () -> new MedicalReportsView(stage).getScene());
                }
                break;
            case 6:
                Navigation.goTo(stage, () -> new AvailabilityScheduleView(stage).getScene());
                break;
            case 7:
                Navigation.goTo(stage, () -> new DoctorProfileView(stage).getScene());
                break;
            case 8:
                Navigation.goTo(stage, () -> new DoctorPaymentsView(stage).getScene());
                break;
            default:
                break;
        }
    }

    private static ImageView createImageView(String path, double width, double height) {
        try {
            InputStream is = DoctorSidebar.class.getResourceAsStream(path);
            if (is != null) {
                ImageView iv = new ImageView(new Image(is));
                iv.setFitWidth(width);
                iv.setFitHeight(height);
                iv.setPreserveRatio(true);
                return iv;
            }
        } catch (Exception e) {
            // Ignore missing icon grace
        }
        return null;
    }
}
