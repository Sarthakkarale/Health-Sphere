package com.healthsphere.model;

import com.healthsphere.util.SessionManager;

public class UserModel {

    private static final UserModel INSTANCE = new UserModel();

    private String customEmail;
    private String customName;

    private UserModel() {
    }

    public static UserModel getInstance() {
        return INSTANCE;
    }

    public String getEmail() {
        if (customEmail != null && !customEmail.isBlank()) {
            return customEmail;
        }
        if (SessionManager.isLoggedIn()) {
            UserProfile profile = SessionManager.getCurrentUser();
            if (profile != null && profile.getEmail() != null && !profile.getEmail().isBlank()) {
                return profile.getEmail();
            }
            String uid = SessionManager.getCurrentUserId();
            if (uid != null && !uid.isBlank()) {
                return uid + "@healthsphere.com";
            }
        }
        return "user@healthsphere.com";
    }

    public void setEmail(String email) {
        this.customEmail = email;
    }

    public String getName() {
        if (customName != null && !customName.isBlank()) {
            return customName;
        }
        if (SessionManager.isLoggedIn()) {
            UserProfile profile = SessionManager.getCurrentUser();
            if (profile != null && profile.getEmail() != null && !profile.getEmail().isBlank()) {
                String email = profile.getEmail();
                if (email.contains("@")) {
                    String part = email.split("@")[0];
                    return Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
                }
            }
            String docName = SessionManager.getDoctorDisplayName();
            if (docName != null && !docName.isBlank()) {
                return docName;
            }
        }
        return "Health-Sphere User";
    }

    public void setName(String name) {
        this.customName = name;
    }
}
