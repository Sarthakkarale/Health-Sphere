package com.healthsphere.util;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Centralized JavaFX image resource loading with caching and bounded decoding. */
public final class ResourceImage {

    private static final String TRANSPARENT_PIXEL =
            "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    private ResourceImage() { }

    public static Image load(String resourcePath) {
        return load(resourcePath, 0, 0, true);
    }

    public static Image load(String resourcePath, double reqWidth, double reqHeight, boolean preserveRatio) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return new Image(TRANSPARENT_PIXEL);
        }

        double targetW = reqWidth > 0 ? reqWidth : 400;
        double targetH = reqHeight > 0 ? reqHeight : 400;

        String cacheKey = resourcePath + "_" + (int)targetW + "x" + (int)targetH;
        Image cached = CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        String path = resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
        InputStream stream = ResourceImage.class.getResourceAsStream(path);

        if (stream == null) {
            System.err.println("[Health-Sphere] Missing image resource: " + path);
            return new Image(TRANSPARENT_PIXEL);
        }

        Image img = new Image(stream, targetW, targetH, preserveRatio, true);

        CACHE.put(cacheKey, img);
        return img;
    }
}
