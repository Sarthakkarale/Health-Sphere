package com.healthsphere.util;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Standardized Summary Card component for HealthSphere.
 * Follows the visual reference from Hospital Doctor Management with distinct background colors per type.
 */
public class SummaryCard {

    public enum CardType {
        BLUE("#DBEAFE", "#93C5FD", "#1E40AF"),
        GREEN("#D1FAE5", "#6EE7B7", "#065F46"),
        PURPLE("#EDE9FE", "#C4B5FD", "#5B21B6"),
        ORANGE("#FEF3C7", "#FDE68A", "#92400E"),
        RED("#FEE2E2", "#FCA5A5", "#991B1B");

        private final String cardBgColor;
        private final String borderColor;
        private final String accentColor;

        CardType(String cardBgColor, String borderColor, String accentColor) {
            this.cardBgColor = cardBgColor;
            this.borderColor = borderColor;
            this.accentColor = accentColor;
        }

        public String getCardBgColor() {
            return cardBgColor;
        }

        public String getBorderColor() {
            return borderColor;
        }

        public String getAccentColor() {
            return accentColor;
        }

        public String getBgColor() {
            return cardBgColor;
        }
    }

    public static class CardNode {
        private final VBox container;
        private final Label valueLabel;
        private final Label subtitleLabel;

        public CardNode(VBox container, Label valueLabel, Label subtitleLabel) {
            this.container = container;
            this.valueLabel = valueLabel;
            this.subtitleLabel = subtitleLabel;
        }

        public VBox getContainer() {
            return container;
        }

        public Label getValueLabel() {
            return valueLabel;
        }

        public Label getSubtitleLabel() {
            return subtitleLabel;
        }

        public void setValue(String value) {
            if (valueLabel != null) {
                valueLabel.setText(value);
            }
        }
    }

    public static VBox create(String title, String value, String subtitle, String icon, CardType type) {
        return createCardNode(title, value, subtitle, icon, type).getContainer();
    }

    public static CardNode createCardNode(String title, String value, String subtitle, String icon, CardType type) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefHeight(120);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Card container styling with distinct background color per CardType
        String normalStyle = String.format(
                "-fx-background-color: %s; " +
                "-fx-background-radius: 14px; " +
                "-fx-border-color: %s; " +
                "-fx-border-radius: 14px; " +
                "-fx-border-width: 1px;",
                type.getCardBgColor(),
                type.getBorderColor()
        );
        card.setStyle(normalStyle);

        DropShadow normalShadow = new DropShadow(8, 0, 2, Color.rgb(15, 23, 42, 0.04));
        DropShadow hoverShadow = new DropShadow(14, 0, 4, Color.rgb(15, 23, 42, 0.12));
        card.setEffect(normalShadow);

        // Top Row: Title + Icon Badge
        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 700; -fx-text-fill: #475569;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label iconLabel = new Label(icon != null ? icon : "★");
        iconLabel.setPrefSize(36, 36);
        iconLabel.setMinSize(36, 36);
        iconLabel.setMaxSize(36, 36);
        iconLabel.setAlignment(Pos.CENTER);
        iconLabel.setStyle(String.format(
                "-fx-background-color: #FFFFFF; -fx-background-radius: 8px; -fx-text-fill: %s; -fx-font-size: 16px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 4, 0, 0, 1);",
                type.getAccentColor()
        ));

        top.getChildren().addAll(titleLabel, spacer, iconLabel);

        // Metric Value Label
        Label valueLabel = new Label(value != null ? value : "0");
        valueLabel.setStyle(String.format("-fx-font-size: 26px; -fx-font-weight: 800; -fx-text-fill: %s;", type.getAccentColor()));

        // Subtitle Label
        Label subtitleLabel = new Label(subtitle != null ? subtitle : "");
        subtitleLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        card.getChildren().addAll(top, valueLabel, subtitleLabel);

        // Smooth Hover Effect (200ms)
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), card);
        scaleIn.setToX(1.02);
        scaleIn.setToY(1.02);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), card);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        card.setOnMouseEntered(e -> {
            card.setEffect(hoverShadow);
            scaleOut.stop();
            scaleIn.playFromStart();
        });

        card.setOnMouseExited(e -> {
            card.setEffect(normalShadow);
            scaleIn.stop();
            scaleOut.playFromStart();
        });

        return new CardNode(card, valueLabel, subtitleLabel);
    }
}
