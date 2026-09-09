package com.healthsphere.util;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Utility class for generating animated Shimmer / Skeleton loading placeholders in JavaFX.
 */
public class ShimmerPlaceholder {

    private ShimmerPlaceholder() {}

    /**
     * Creates a basic animated shimmer block with custom size and border radius.
     */
    public static Region createBlock(double width, double height, double borderRadius) {
        Region block = new Region();
        block.setPrefSize(width, height);
        if (width > 0) {
            block.setMinWidth(width);
            block.setMaxWidth(width);
        }
        block.setMinHeight(height);
        block.setMaxHeight(height);
        block.setStyle(String.format("-fx-background-color: #E2E8F0; -fx-background-radius: %.0fpx;", borderRadius));

        applyPulseAnimation(block);
        return block;
    }

    /**
     * Creates a block that expands horizontally.
     */
    public static Region createFlexibleBlock(double height, double borderRadius) {
        Region block = new Region();
        block.setMinHeight(height);
        block.setPrefHeight(height);
        block.setMaxHeight(height);
        HBox.setHgrow(block, Priority.ALWAYS);
        VBox.setVgrow(block, Priority.ALWAYS);
        block.setStyle(String.format("-fx-background-color: #E2E8F0; -fx-background-radius: %.0fpx;", borderRadius));

        applyPulseAnimation(block);
        return block;
    }

    /**
     * Creates a shimmer skeleton representation of a Statistic/Metrics Card.
     */
    public static VBox createStatCardShimmer() {
        VBox card = new VBox(12);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);
        Region iconBlock = createBlock(40, 40, 20);
        Region titleBlock = createBlock(100, 16, 4);
        topRow.getChildren().addAll(iconBlock, titleBlock);

        Region valueBlock = createBlock(140, 28, 6);
        Region subtextBlock = createBlock(180, 12, 4);

        card.getChildren().addAll(topRow, valueBlock, subtextBlock);
        return card;
    }

    /**
     * Creates a shimmer skeleton list row item.
     */
    public static HBox createListRowShimmer() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 18, 14, 18));
        row.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10px; -fx-border-color: #F1F5F9; -fx-border-radius: 10px;");

        Region avatar = createBlock(40, 40, 20);
        VBox textGroup = new VBox(8);
        Region line1 = createBlock(160, 14, 4);
        Region line2 = createBlock(110, 12, 4);
        textGroup.getChildren().addAll(line1, line2);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Region badge = createBlock(80, 24, 12);

        row.getChildren().addAll(avatar, textGroup, spacer, badge);
        return row;
    }

    /**
     * Creates a shimmer container with multiple list rows.
     */
    public static VBox createListShimmer(int rowCount) {
        VBox container = new VBox(12);
        for (int i = 0; i < rowCount; i++) {
            container.getChildren().add(createListRowShimmer());
        }
        return container;
    }

    /**
     * Creates a shimmer appointment card block.
     */
    public static VBox createAppointmentCardShimmer() {
        VBox card = new VBox(14);
        card.setPadding(new Insets(18));
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 12px; -fx-border-color: #E2E8F0; -fx-border-radius: 12px;");

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Region avatar = createBlock(44, 44, 22);
        VBox nameBox = new VBox(6);
        Region name = createBlock(140, 16, 4);
        Region id = createBlock(90, 12, 4);
        nameBox.getChildren().addAll(name, id);
        header.getChildren().addAll(avatar, nameBox);

        Region divider = createBlock(264, 2, 1);
        Region info1 = createBlock(220, 14, 4);
        Region info2 = createBlock(180, 14, 4);

        HBox btnRow = new HBox(10);
        Region btn1 = createBlock(125, 32, 6);
        Region btn2 = createBlock(125, 32, 6);
        btnRow.getChildren().addAll(btn1, btn2);

        card.getChildren().addAll(header, divider, info1, info2, btnRow);
        return card;
    }

    /**
     * Applies smooth fade pulsing animation to simulate shimmer.
     * Auto-stops when the node is detached from its parent or scene graph to prevent memory leaks.
     */
    private static void applyPulseAnimation(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(650), node);
        fade.setFromValue(0.4);
        fade.setToValue(0.9);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();

        node.parentProperty().addListener((obs, oldParent, newParent) -> {
            if (newParent == null) {
                try {
                    fade.stop();
                } catch (Exception ignored) {}
            }
        });

        node.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                try {
                    fade.stop();
                } catch (Exception ignored) {}
            }
        });
    }
}
