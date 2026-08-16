package com.healthsphere.util;

import com.healthsphere.model.AuthenticationResponse;

public final class SessionManager {

    private static AuthenticationResponse currentUser;

    private SessionManager() {
        // Prevent object creation.
    }

    public static void createSession(
            AuthenticationResponse authenticationResponse) {

        if (authenticationResponse == null) {
            throw new IllegalArgumentException(
                    "Authentication response cannot be null."
            );
        }

        currentUser = authenticationResponse;
    }

    public static AuthenticationResponse getCurrentUser() {

        if (currentUser == null) {
            throw new IllegalStateException(
                    "No active user session."
            );
        }

        return currentUser;
    }

    public static boolean isLoggedIn() {

        return currentUser != null;
    }

    public static void clearSession() {

        currentUser = null;
    }
}