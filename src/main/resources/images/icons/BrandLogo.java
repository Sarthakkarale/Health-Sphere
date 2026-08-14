package com.healthsphere.components;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Pure JavaFX Vector Logo for Health-Sphere.
 * Renders a circular brand badge without needing external images.
 */
public class BrandLogo extends VBox {

    public BrandLogo() {
        this(120);
    }

    public BrandLogo(double size) {
        this.setAlignment(Pos.CENTER);
        this.setSpacing(6);

        // Circular Emblem Container
        StackPane emblem = new StackPane();
        emblem.setPrefSize(size, size);
        emblem.setMaxSize(size, size);

        // Background Circle
        Circle bgCircle = new Circle(size / 2);
        bgCircle.setFill(Color.WHITE);
        bgCircle.setStroke(Color.web("#E2E8F0"));
        bgCircle.setStrokeWidth(1.5);

        // Accent Ring
        Circle ringArc = new Circle(size * 0.42);
        ringArc.setFill(Color.TRANSPARENT);
        ringArc.setStroke(Color.web("#0D9488"));
        ringArc.setStrokeWidth(2);

        // Central Medical Cross Icon
        SVGPath crossIcon = new SVGPath();
        crossIcon.setContent("M19 10.5h-5.5V5c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v5.5H5c-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5h5.5V19c0 .83.67 1.5 1.5 1.5s1.5-.67 1.5-1.5v-5.5H19c.83 0 1.5-.67 1.5-1.5s-.67-1.5-1.5-1.5z");
        
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0256D0")),
                new Stop(1, Color.web("#0D9488"))
        );
        crossIcon.setFill(gradient);
        crossIcon.setScaleX(size / 60.0);
        crossIcon.setScaleY(size / 60.0);

        emblem.getChildren().addAll(bgCircle, ringArc, crossIcon);

        // Typography
        Text brandName = new Text("HEALTH SPHERE");
        brandName.setFont(Font.font("Segoe UI", FontWeight.BOLD, Math.max(10, size * 0.1)));
        brandName.setFill(Color.web("#0256D0"));

        Text tagLine = new Text("CARE • CONNECT • COMPARE");
        tagLine.setFont(Font.font("Segoe UI", FontWeight.NORMAL, Math.max(7, size * 0.055)));
        tagLine.setFill(Color.web("#64748B"));

        this.getChildren().addAll(emblem, brandName, tagLine);
    }
}