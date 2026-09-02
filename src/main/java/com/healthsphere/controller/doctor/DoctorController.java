package com.healthsphere.controller.doctor;

import com.healthsphere.dao.doctor.DoctorDAO;
import com.healthsphere.model.DoctorProfile;
import com.healthsphere.util.SessionManager;

public class DoctorController {

    private final DoctorDAO doctorDAO;

    public DoctorController() {
        this.doctorDAO = new DoctorDAO();
    }

    /**
     * Gets the currently logged-in doctor's profile.
     */
    public DoctorProfile getCurrentDoctorProfile() {

        String uid = getCurrentDoctorUid();

        return doctorDAO.getDoctorProfile(uid);
    }

    /**
     * Updates the currently logged-in doctor's profile.
     */
    public void updateCurrentDoctorProfile(
            DoctorProfile doctorProfile) {

        if (doctorProfile == null) {
            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

        String uid = getCurrentDoctorUid();

        // Never trust a UID supplied by the UI.
        doctorProfile.setUid(uid);

        doctorDAO.updateDoctorProfile(
                doctorProfile
        );
    }

    /**
     * Gets the UID of the currently authenticated user.
     */
    private String getCurrentDoctorUid() {

        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "No active user session."
            );
        }

        String uid =
                SessionManager.getInstance()
                        .getAuthenticationResponse()
                        .getUid();

        if (uid == null || uid.isBlank()) {
            throw new IllegalStateException(
                    "Current user UID is unavailable."
            );
        }

        return uid;
    }
}