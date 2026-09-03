
package com.healthsphere.controller.patient;

import com.healthsphere.dao.authentication.PatientDAO;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.util.SessionManager;

public class PatientController {

    private final PatientDAO patientDAO;

    /*
     * Cached profile for the currently logged-in patient.
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

        if (cachedPatientProfile != null &&
                uid.equals(cachedPatientProfile.getUid())) {

            return cachedPatientProfile;
        }

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

        PatientProfile existingProfile;

        if (cachedPatientProfile != null &&
                uid.equals(cachedPatientProfile.getUid())) {

            existingProfile = cachedPatientProfile;

        } else {

            existingProfile =
                    patientDAO.getPatientProfile(uid);
        }

        String[] nameParts =
                fullName.trim().split("\\s+", 2);

        String firstName =
                nameParts[0];

        String lastName =
                nameParts.length > 1
                        ? nameParts[1]
                        : "";

        existingProfile.setFirstName(firstName);
        existingProfile.setLastName(lastName);
        existingProfile.setEmail(email.trim());
        existingProfile.setPhone(phone.trim());
        existingProfile.setAddress(city.trim());

        patientDAO.updatePatientProfile(
                existingProfile
        );

        cachedPatientProfile = existingProfile;

        return existingProfile;
    }

    // ============================================================
    // UPDATE HEALTH PASSPORT PROFILE
    // ============================================================

    public PatientProfile updateHealthPassportProfile(
            String fullName,
            String email,
            String phone,
            String address,
            String dateOfBirth,
            String gender,
            String bloodGroup,
            String emergencyContact) {

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

        if (address == null ||
                address.isBlank()) {

            throw new IllegalArgumentException(
                    "Address cannot be empty."
            );
        }

        PatientProfile existingProfile;

        if (cachedPatientProfile != null &&
                uid.equals(cachedPatientProfile.getUid())) {

            existingProfile =
                    cachedPatientProfile;

        } else {

            existingProfile =
                    patientDAO.getPatientProfile(uid);
        }

        String[] nameParts =
                fullName.trim()
                        .split("\\s+", 2);

        existingProfile.setFirstName(
                nameParts[0]
        );

        existingProfile.setLastName(
                nameParts.length > 1
                        ? nameParts[1]
                        : ""
        );

        existingProfile.setEmail(
                email.trim()
        );

        existingProfile.setPhone(
                phone.trim()
        );

        existingProfile.setAddress(
                address.trim()
        );

        existingProfile.setDateOfBirth(
                clean(dateOfBirth)
        );

        existingProfile.setGender(
                clean(gender)
        );

        existingProfile.setBloodGroup(
                clean(bloodGroup)
        );

        existingProfile.setEmergencyContact(
                clean(emergencyContact)
        );

        patientDAO.updatePatientProfile(
                existingProfile
        );

        cachedPatientProfile =
                existingProfile;

        return existingProfile;
    }

    // ============================================================
    // UPDATE HEALTH STATUS
    // ============================================================

    public PatientProfile updateHealthStatus(
            String heartRate,
            String bloodPressure,
            String oxygenLevel,
            String lastHealthCheck) {

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

        PatientProfile existingProfile;

        if (cachedPatientProfile != null &&
                uid.equals(cachedPatientProfile.getUid())) {

            existingProfile =
                    cachedPatientProfile;

        } else {

            existingProfile =
                    patientDAO.getPatientProfile(uid);
        }

        existingProfile.setHeartRate(
                clean(heartRate)
        );

        existingProfile.setBloodPressure(
                clean(bloodPressure)
        );

        existingProfile.setOxygenLevel(
                clean(oxygenLevel)
        );

        existingProfile.setLastHealthCheck(
                clean(lastHealthCheck)
        );

        patientDAO.updatePatientProfile(
                existingProfile
        );

        cachedPatientProfile =
                existingProfile;

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

    // ============================================================
    // CLEAN VALUE
    // ============================================================

    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }
}

