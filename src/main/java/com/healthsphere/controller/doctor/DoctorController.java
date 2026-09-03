package com.healthsphere.controller.doctor;

import com.healthsphere.dao.doctor.DoctorDAO;
import com.healthsphere.model.DoctorProfile;

public class DoctorController {

    private final DoctorDAO doctorDAO;

    public DoctorController() {
        this.doctorDAO = new DoctorDAO();
    }

    // ============================================================
    // GET DOCTOR PROFILE
    // ============================================================

    public DoctorProfile getDoctorProfile(String doctorUid) {

        if (doctorUid == null || doctorUid.isBlank()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be null or empty."
            );
        }

        return doctorDAO.getDoctorProfile(
                doctorUid
        );
    }

    // ============================================================
    // UPDATE DOCTOR PROFILE
    // ============================================================

    public void updateDoctorProfile(
            DoctorProfile doctorProfile) {

        if (doctorProfile == null) {

            throw new IllegalArgumentException(
                    "Doctor profile cannot be null."
            );
        }

        if (doctorProfile.getUid() == null
                || doctorProfile.getUid().isBlank()) {

            throw new IllegalArgumentException(
                    "Doctor UID cannot be null or empty."
            );
        }

        doctorDAO.updateDoctorProfile(
                doctorProfile
        );
    }
}