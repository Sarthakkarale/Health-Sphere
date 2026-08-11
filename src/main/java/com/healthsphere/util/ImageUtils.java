package com.healthsphere.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class ImageUtils {

    // Default Images Directory in Resources: src/main/resources/images/
    private static final String IMAGE_DIR = "/images/";

    /**
     * Loads an Image safely from the resources folder.
     * @param fileName File name with extension (e.g., "doctor_avatar.png")
     * @return Image object or null if not found
     */
    public static Image loadImage(String fileName) {
        try {
            InputStream is = ImageUtils.class.getResourceAsStream(IMAGE_DIR + fileName);
            if (is != null) {
                return new Image(is);
            } else {
                System.err.println("⚠️ ImageUtils: Image not found at classpath -> " + IMAGE_DIR + fileName);
            }
        } catch (Exception e) {
            System.err.println("❌ ImageUtils Error loading image [" + fileName + "]: " + e.getMessage());
        }
        return null;
    }

    /**
     * Creates a circular Avatar View for Doctor/User Profiles with Fallback Initials
     * @param imageName File name (e.g., "user.png")
     * @param initials Fallback text if image missing (e.g., "PP" for Prajwal Patil)
     * @param radius Avatar Circle Size
     * @return StackPane containing circular image or styled fallback avatar
     */
    public static StackPane createAvatar(String imageName, String initials, double radius) {
        StackPane avatarPane = new StackPane();

        Image img = loadImage(imageName);
        if (img != null) {
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(radius * 2);
            imgView.setFitHeight(radius * 2);
            imgView.setPreserveRatio(true);

            // Clip into circle
            Circle clip = new Circle(radius, radius, radius);
            imgView.setClip(clip);
            avatarPane.getChildren().add(imgView);
        } else {
            // Fallback Neon Avatar Circle
            Circle circle = new Circle(radius, Color.web("#312E81"));
            circle.setStroke(Color.web("#6366F1"));
            circle.setStrokeWidth(1.5);

            Label label = new Label(initials != null ? initials.toUpperCase() : "US");
            label.setFont(Font.font("Segoe UI", FontWeight.BOLD, radius * 0.8));
            label.setTextFill(Color.web("#818CF8"));

            avatarPane.getChildren().addAll(circle, label);
        }

        return avatarPane;
    }

    /**
     * Creates an Icon/Badge with custom sizing
     */
    public static ImageView createIconView(String fileName, double size) {
        Image img = loadImage(fileName);
        ImageView view = new ImageView();
        if (img != null) {
            view.setImage(img);
            view.setFitWidth(size);
            view.setFitHeight(size);
            view.setPreserveRatio(true);
        }
        return view;
    }
}