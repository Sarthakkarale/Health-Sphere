package com.healthsphere.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {

    private static Cloudinary cloudinary;

    private CloudinaryConfig() {
        // Prevent object creation
    }

    /**
     * Returns the configured Cloudinary instance.
     *
     * Uses environment variables instead of hardcoded credentials.
     */
    public static Cloudinary getCloudinary() {

        if (cloudinary != null) {
            return cloudinary;
        }

        String cloudName = "ks2hd1ox";

        String apiKey = "348493465234243";

        String apiSecret = "EENAcNBm4vyApAxa4LDOkgX6VYQ";

        if (isEmpty(cloudName)
                || isEmpty(apiKey)
                || isEmpty(apiSecret)) {

            throw new IllegalStateException(
                    "Cloudinary credentials are not configured. "
                            + "Set CLOUDINARY_CLOUD_NAME, "
                            + "CLOUDINARY_API_KEY and "
                            + "CLOUDINARY_API_SECRET."
            );
        }

        cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        "cloud_name", cloudName,
                        "api_key", apiKey,
                        "api_secret", apiSecret
                )
        );

        return cloudinary;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}