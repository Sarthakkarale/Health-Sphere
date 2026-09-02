package com.healthsphere.util;

import com.healthsphere.model.AuthenticationResponse;

/**
 * SessionManager
 *
 * Stores the currently authenticated user's temporary
 * session information in memory.
 *
 * Firebase / Firestore remains the persistent source of truth.
 *
 * This class intentionally uses static methods so all
 * controllers and views can access the same current session
 * without creating multiple session objects.
 */
public final class SessionManager {

    /*
     * Currently authenticated user/session.
     *
     * volatile ensures that changes to the session reference
     * are visible across threads.
     */
    private static volatile AuthenticationResponse currentUser;

    /*
     * Private constructor prevents creating SessionManager objects.
     */
    private SessionManager() {
        throw new UnsupportedOperationException(
                "SessionManager cannot be instantiated."
        );
    }

    // =========================================================
    // CREATE SESSION
    // =========================================================

    /**
     * Creates an in-memory session for the authenticated user.
     *
     * @param authenticationResponse authentication result
     */
    public static synchronized void createSession(
            AuthenticationResponse authenticationResponse
    ) {

        if (authenticationResponse == null) {

            throw new IllegalArgumentException(
                    "Authentication response cannot be null."
            );
        }

        if (authenticationResponse.getUid() == null
                ||
                authenticationResponse.getUid()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Authenticated user UID cannot be empty."
            );
        }

        currentUser =
                authenticationResponse;
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================

    /**
     * Returns the currently authenticated user.
     *
     * @return current authentication response,
     *         or null when no session exists
     */
    public static AuthenticationResponse
    getCurrentUser() {

        return currentUser;
    }

    // =========================================================
    // CHECK SESSION
    // =========================================================

    /**
     * Checks whether an authenticated session currently exists.
     *
     * @return true if a session exists
     */
    public static boolean isLoggedIn() {

        return currentUser != null
                &&
                currentUser.getUid() != null
                &&
                !currentUser.getUid()
                        .trim()
                        .isEmpty();
    }

    // =========================================================
    // GET CURRENT USER UID
    // =========================================================

    /**
     * Returns the UID of the currently authenticated user.
     *
     * @return UID, or null if no session exists
     */
    public static String getCurrentUserId() {

        if (!isLoggedIn()) {
            return null;
        }

        return currentUser
                .getUid();
    }

    // =========================================================
    // CLEAR SESSION
    // =========================================================

    /**
     * Clears the current in-memory session.
     *
     * Firebase Authentication / Firestore data is not deleted.
     * Only the temporary local session is removed.
     */
    public static synchronized void clearSession() {

        currentUser = null;
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    /**
     * Alias for clearSession().
     *
     * This can be used by UI/controllers when logging out.
     */
    public static synchronized void logout() {

        clearSession();
    }

    // =========================================================
    // REQUIRE AUTHENTICATED SESSION
    // =========================================================

    /**
     * Returns the current authenticated user.
     *
     * Throws an exception if no session exists.
     *
     * Useful for protected controller operations.
     *
     * @return authenticated user
     */
    public static AuthenticationResponse
    requireCurrentUser() {

        if (!isLoggedIn()) {

            throw new IllegalStateException(
                    "No authenticated user session found."
            );
        }

        return currentUser;
    }

    // =========================================================
    // REQUIRE USER ID
    // =========================================================

    /**
     * Returns the current user's UID.
     *
     * Throws an exception if no valid session exists.
     *
     * @return authenticated user's UID
     */
    public static String requireCurrentUserId() {

        if (!isLoggedIn()) {

            throw new IllegalStateException(
                    "No authenticated user session found."
            );
        }

        return currentUser
                .getUid();
    }
}