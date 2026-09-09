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

        String cacheKey = resourcePath + "_" + (int)reqWidth + "x" + (int)reqHeight;
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

        Image img;
        if (reqWidth > 0 && reqHeight > 0) {
            img = new Image(stream, reqWidth, reqHeight, preserveRatio, true);
        } else {
            img = new Image(stream);
        }

        CACHE.put(cacheKey, img);
        return img;
    }
}
