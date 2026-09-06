package com.healthsphere.util;

import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.UserProfile;

public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private AuthenticationResponse authenticationResponse;
    private static UserProfile currentUserProfile;
    private static volatile AuthenticationResponse currentUser;
    private static String cachedDoctorName = null;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public static synchronized void createSession(AuthenticationResponse authResponse) {
        if (authResponse == null) {
            throw new IllegalArgumentException("Authentication response cannot be null.");
        }

        SessionManager session = getInstance();
        session.authenticationResponse = authResponse;
        currentUser = authResponse;

        currentUserProfile = new UserProfile(
                authResponse.getUid(),
                authResponse.getEmail(),
                null,
                null
        );
    }

    public AuthenticationResponse getAuthenticationResponse() {
        if (authenticationResponse == null) {
            throw new IllegalStateException("No active user session.");
        }
        return authenticationResponse;
    }

    public static UserProfile getCurrentUser() {
        if (currentUserProfile != null) {
            return currentUserProfile;
        }
        if (currentUser != null) {
            return new UserProfile(currentUser.getUid(), currentUser.getEmail(), null, null);
        }
        return null;
    }

    public static AuthenticationResponse getCurrentAuthUser() {
        return currentUser;
    }

    public void setCurrentUser(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("User profile cannot be null.");
        }
        currentUserProfile = userProfile;
    }

    public static boolean isLoggedIn() {
        return currentUser != null || currentUserProfile != null || INSTANCE.authenticationResponse != null;
    }

    public static String getCurrentUserId() {
        if (!isLoggedIn()) {
            return null;
        }
        if (currentUserProfile != null) {
            return currentUserProfile.getUid();
        }
        if (currentUser != null) {
            return currentUser.getUid();
        }
        return null;
    }

    public static String getPatientUid() {
        return getCurrentUserId();
    }

    public static String getDoctorUid() {
        return getCurrentUserId();
    }

    public static String getHospitalUid() {
        return getCurrentUserId();
    }

    public static String getDoctorDisplayName() {
        if (cachedDoctorName != null && !cachedDoctorName.isBlank()) {
            return cachedDoctorName;
        }

        try {
            UserProfile user = getCurrentUser();
            if (user != null && user.getEmail() != null) {
                String email = user.getEmail().trim();
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

    public static synchronized void clearSession() {
        SessionManager session = getInstance();
        session.authenticationResponse = null;
        currentUserProfile = null;
        currentUser = null;
        cachedDoctorName = null;
    }

    public static synchronized void logout() {
        clearSession();
    }
}