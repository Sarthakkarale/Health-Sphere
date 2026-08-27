package com.healthsphere.util;

import javafx.scene.image.Image;

import java.io.InputStream;

/** Centralized JavaFX image resource loading. Paths are always classpath-relative. */
public final class ResourceImage {

    private static final String TRANSPARENT_PIXEL =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private ResourceImage() { }

    public static Image load(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return new Image(TRANSPARENT_PIXEL);
        }

        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        InputStream stream = ResourceImage.class.getResourceAsStream(path);

        if (stream == null) {
            System.err.println("[Health-Sphere] Missing image resource: " + path);
            return new Image(TRANSPARENT_PIXEL);
        }

        return new Image(stream);
    }
}
