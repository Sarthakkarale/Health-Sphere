package com.healthsphere.controller.patient;

import com.healthsphere.dao.authentication.PatientDAO;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;

public class PatientController {

    private final PatientDAO patientDAO;

    /*
     * Cached profile for the currently logged-in patient.
     *
     * This prevents every Patient screen from making another
     * Firebase read for the same profile.
     */
    private static PatientProfile cachedPatientProfile;

    public PatientController() {
        this.patientDAO = new PatientDAO();
    }

    // ============================================================
    // GET CURRENT PATIENT PROFILE
    // ============================================================

    public PatientProfile getCurrentPatientProfile() {

        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "No active user session."
            );
        }

        String uid =
                SessionManager.getCurrentUser().getUid();

        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException(
                    "Current user UID is missing."
            );
        }

        /*
         * Return cached profile if it belongs to the
         * currently logged-in user.
         */
        if (cachedPatientProfile != null &&
                uid.equals(cachedPatientProfile.getUid())) {

            return cachedPatientProfile;
        }

        /*
         * Only read Firebase when the profile is not
         * already cached.
         */
        PatientProfile profile =
                patientDAO.getPatientProfile(uid);

        cachedPatientProfile = profile;

        return profile;
    }

    // ============================================================
    // UPDATE CURRENT PATIENT PROFILE
    // ============================================================

    public PatientProfile updateCurrentPatientProfile(
            String fullName,
            String email,
            String phone,
            String city) {

        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "No active user session."
            );
        }

        String uid =
                SessionManager.getCurrentUser().getUid();

        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException(
                    "Current user UID is missing."
            );
        }

        validateProfileInput(
                fullName,
                email,
                phone,
                city
        );

        /*
         * Use the cached profile when available.
         * This avoids another Firebase read after
         * the profile was already loaded.
         */
        PatientProfile existingProfile;

        if (cachedPatientProfile != null &&
                uid.equals(cachedPatientProfile.getUid())) {

            existingProfile = cachedPatientProfile;

        } else {

            existingProfile =
                    patientDAO.getPatientProfile(uid);
        }

        // --------------------------------------------------------
        // Split full name into first + last name
        // --------------------------------------------------------

        String[] nameParts =
                fullName.trim().split("\\s+", 2);

        String firstName =
                nameParts[0];

        String lastName =
                nameParts.length > 1
                        ? nameParts[1]
                        : "";

        // --------------------------------------------------------
        // Update only fields represented by ProfileSettings UI
        // --------------------------------------------------------

        existingProfile.setFirstName(firstName);
        existingProfile.setLastName(lastName);
        existingProfile.setEmail(email.trim());
        existingProfile.setPhone(phone.trim());
        existingProfile.setAddress(city.trim());

        // --------------------------------------------------------
        // Save to Firebase
        // --------------------------------------------------------

        patientDAO.updatePatientProfile(
                existingProfile
        );

        /*
         * Update cache so other Patient screens immediately
         * receive the latest profile without another Firebase read.
         */
        cachedPatientProfile = existingProfile;

        return existingProfile;
    }

    // ============================================================
    // CLEAR CACHED PROFILE
    // ============================================================

    public static void clearCachedPatientProfile() {

        cachedPatientProfile = null;
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateProfileInput(
            String fullName,
            String email,
            String phone,
            String city) {

        if (fullName == null ||
                fullName.isBlank()) {

            throw new IllegalArgumentException(
                    "Full name cannot be empty."
            );
        }

        if (email == null ||
                email.isBlank()) {

            throw new IllegalArgumentException(
                    "Email cannot be empty."
            );
        }

        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            throw new IllegalArgumentException(
                    "Please enter a valid email address."
            );
        }

        if (phone == null ||
                phone.isBlank()) {

            throw new IllegalArgumentException(
                    "Phone number cannot be empty."
            );
        }

        if (city == null ||
                city.isBlank()) {

            throw new IllegalArgumentException(
                    "City cannot be empty."
            );
        }
    }
}