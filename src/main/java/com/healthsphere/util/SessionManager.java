package com.healthsphere.util;

import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.UserProfile;

public final class SessionManager {

    // ============================================================
    // SINGLETON INSTANCE
    // ============================================================

    private static final SessionManager INSTANCE =
            new SessionManager();

    // ============================================================
    // SESSION DATA
    // ============================================================

    private AuthenticationResponse authenticationResponse;

    private static UserProfile currentUser;

    // ============================================================
    // PRIVATE CONSTRUCTOR
    // ============================================================

    private SessionManager() {
        // Prevent object creation from outside.
    }

    // ============================================================
    // GET SINGLETON INSTANCE
    // ============================================================

    public static SessionManager getInstance() {

        return INSTANCE;
    }

    // ============================================================
    // CREATE SESSION
    // ============================================================

    public static void createSession(
            AuthenticationResponse authenticationResponse) {

        if (authenticationResponse == null) {
            throw new IllegalArgumentException(
                    "Authentication response cannot be null."
            );
        }

        SessionManager session =
                getInstance();

        session.authenticationResponse =
                authenticationResponse;

        currentUser = new UserProfile(
                authenticationResponse.getUid(),
                authenticationResponse.getEmail(),
                null,
                null
        );
    }

    // ============================================================
    // AUTHENTICATION RESPONSE
    // ============================================================

    public AuthenticationResponse getAuthenticationResponse() {

        if (authenticationResponse == null) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        return authenticationResponse;
    }

    // ============================================================
    // CURRENT USER PROFILE
    // ============================================================

    public void setCurrentUser(
            UserProfile userProfile) {

        if (userProfile == null) {

            throw new IllegalArgumentException(
                    "User profile cannot be null."
            );
        }

        this.currentUser =
                userProfile;
    }

    public static UserProfile getCurrentUser() {

        if (currentUser == null) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        return currentUser;
    }

    // ============================================================
    // LOGIN STATUS
    // ============================================================

    public static boolean isLoggedIn() {

        return INSTANCE.authenticationResponse != null;
    }

    private static String cachedDoctorName = null;

    public static String getDoctorDisplayName() {
        if (cachedDoctorName != null && !cachedDoctorName.isBlank()) {
            return cachedDoctorName;
        }

        try {
            if (currentUser != null && currentUser.getEmail() != null) {
                String email = currentUser.getEmail().trim();
                if (email.contains("@")) {
                    String name = email.split("@")[0];
                    if (name.contains(".")) {
                        String[] parts = name.split("\\.");
                        StringBuilder sb = new StringBuilder("Dr. ");
                        for (String p : parts) {
                            if (!p.isBlank()) {
                                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1).toLowerCase()).append(" ");
                            }
                        }
                        cachedDoctorName = sb.toString().trim();
                        return cachedDoctorName;
                    } else if (!name.isBlank()) {
                        cachedDoctorName = "Dr. " + Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
                        return cachedDoctorName;
                    }
                }
            }
        } catch (Exception ignored) {}

        return "Dr. Medical Practitioner";
    }

    public static void setDoctorDisplayName(String name) {
        if (name != null && !name.isBlank()) {
            cachedDoctorName = name;
        }
    }

    // ============================================================
    // LOGOUT / CLEAR SESSION
    // ============================================================

    public static void clearSession() {

        SessionManager session =
                getInstance();

        session.authenticationResponse = null;
        session.currentUser = null;
        cachedDoctorName = null;
    }
}