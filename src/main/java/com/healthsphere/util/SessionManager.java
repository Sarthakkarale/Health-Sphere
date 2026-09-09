package com.healthsphere.util;

import com.google.cloud.firestore.ListenerRegistration;
import com.healthsphere.model.AuthenticationResponse;
import com.healthsphere.model.UserProfile;
import javafx.concurrent.Task;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private AuthenticationResponse authenticationResponse;
    private static UserProfile currentUserProfile;
    private static volatile AuthenticationResponse currentUser;
    private static String cachedDoctorName = null;

    // Active listeners & tasks tracked for automatic cleanup on logout
    private static final Set<ListenerRegistration> ACTIVE_LISTENERS = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static final Set<Task<?>> ACTIVE_TASKS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public static void registerListener(ListenerRegistration listener) {
        if (listener != null) {
            ACTIVE_LISTENERS.add(listener);
        }
    }

    public static void unregisterListener(ListenerRegistration listener) {
        if (listener != null) {
            ACTIVE_LISTENERS.remove(listener);
        }
    }

    public static void registerTask(Task<?> task) {
        if (task != null) {
            ACTIVE_TASKS.add(task);
            task.onSucceededProperty().addListener((obs, oldVal, newVal) -> ACTIVE_TASKS.remove(task));
            task.onFailedProperty().addListener((obs, oldVal, newVal) -> ACTIVE_TASKS.remove(task));
            task.onCancelledProperty().addListener((obs, oldVal, newVal) -> ACTIVE_TASKS.remove(task));
        }
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

        String docUid = getDoctorUid();
        if (docUid != null && !docUid.isBlank()) {
            try {
                com.healthsphere.dao.authentication.DoctorDAO dao = new com.healthsphere.dao.authentication.DoctorDAO();
                com.healthsphere.model.DoctorProfile prof = dao.getDoctorProfile(docUid);
                if (prof != null) {
                    setDoctorProfile(prof);
                    if (cachedDoctorName != null && !cachedDoctorName.isBlank()) {
                        return cachedDoctorName;
                    }
                }
            } catch (Exception ignored) {}
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
            if (!name.trim().startsWith("Dr.")) {
                name = "Dr. " + name.trim();
            }
            cachedDoctorName = name.trim();
        }
    }

    public static void setDoctorProfile(com.healthsphere.model.DoctorProfile profile) {
        if (profile != null) {
            String firstName = profile.getFirstName() != null ? profile.getFirstName().trim() : "";
            String lastName = profile.getLastName() != null ? profile.getLastName().trim() : "";
            String name = (firstName + " " + lastName).trim();
            if (name.isBlank()) {
                name = "Medical Practitioner";
            }
            setDoctorDisplayName(name);
        }
    }

    public static synchronized void clearSession() {
        // Detach all registered Firestore snapshot listeners
        for (ListenerRegistration listener : ACTIVE_LISTENERS) {
            try {
                if (listener != null) {
                    listener.remove();
                }
            } catch (Exception ignored) {}
        }
        ACTIVE_LISTENERS.clear();

        // Cancel running background tasks
        for (Task<?> task : ACTIVE_TASKS) {
            try {
                if (task != null && task.isRunning()) {
                    task.cancel(true);
                }
            } catch (Exception ignored) {}
        }
        ACTIVE_TASKS.clear();

        // Clear cached patient profile, DAO caches and user model state
        try {
            com.healthsphere.controller.patient.PatientController.clearCachedPatientProfile();
            com.healthsphere.controller.patient.HospitalController.clearCache();
            com.healthsphere.dao.authentication.DoctorDAO.clearCache();
            com.healthsphere.dao.doctor.DoctorDAO.clearCache();
            com.healthsphere.view.Patient.PatientUI.clearViewCache();
        } catch (Exception ignored) {}

        try {
            com.healthsphere.model.UserModel.getInstance().setEmail(null);
            com.healthsphere.model.UserModel.getInstance().setName(null);
        } catch (Exception ignored) {}

        // Clear scene navigation history
        Navigation.clearHistory();

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