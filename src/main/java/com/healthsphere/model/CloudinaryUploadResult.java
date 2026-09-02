package com.healthsphere.model;

/**
 * Contains the result of a successful Cloudinary upload.
 */
public class CloudinaryUploadResult {

    private String publicId;
    private String secureUrl;
    private String resourceType;
    private String fileName;

    public CloudinaryUploadResult() {
    }

    public CloudinaryUploadResult(
            String publicId,
            String secureUrl,
            String resourceType,
            String fileName) {

        this.publicId = publicId;
        this.secureUrl = secureUrl;
        this.resourceType = resourceType;
        this.fileName = fileName;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getSecureUrl() {
        return secureUrl;
    }

    public void setSecureUrl(String secureUrl) {
        this.secureUrl = secureUrl;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}