package com.healthsphere.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class UIUtils {

    // Color Palette Constants
    public static final String COLOR_BG_DARK = "#0B0F19";
    public static final String COLOR_CARD_DARK = "#1E293B";
    public static final String COLOR_SIDEBAR = "#0F172A";
    public static final String COLOR_ACCENT = "#6366F1";
    public static final String COLOR_TEXT_MAIN = "#F8FAFC";
    public static final String COLOR_TEXT_MUTED = "#94A3B8";
    public static final String COLOR_SUCCESS = "#10B981";
    public static final String COLOR_DANGER = "#F43F5E";
    public static final String COLOR_WARNING = "#F59E0B";

    public static DropShadow getGlowEffect(String colorHex, double radius) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(colorHex, 0.45));
        glow.setRadius(radius);
        glow.setOffsetY(0);
        return glow;
    }

    public static DropShadow getCardShadow() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(15, 23, 42, 0.3));
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        return shadow;
    }

    public static void showInfoDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}