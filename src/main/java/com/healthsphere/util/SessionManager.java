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

    // ============================================================
    // LOGOUT / CLEAR SESSION
    // ============================================================

    public static void clearSession() {

        SessionManager session =
                getInstance();

        session.authenticationResponse = null;
        session.currentUser = null;
    }
}