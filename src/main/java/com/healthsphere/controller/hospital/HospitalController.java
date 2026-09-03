package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.HospitalDAO;
import com.healthsphere.model.HospitalProfile;

public class HospitalController {

    private final HospitalDAO hospitalDAO;

    public HospitalController() {
        this.hospitalDAO = new HospitalDAO();
    }

    /**
     * Retrieves the currently logged-in hospital's profile.
     */
    public HospitalProfile getHospitalProfile() {

        return hospitalDAO.getHospitalProfile();
    }

    /**
     * Saves a new hospital profile.
     */
    public void saveHospitalProfile(
            HospitalProfile hospitalProfile) {

        hospitalDAO.saveHospitalProfile(hospitalProfile);
    }

    /**
     * Updates the currently logged-in hospital's profile.
     */
    public void updateHospitalProfile(
            HospitalProfile hospitalProfile) {

        hospitalDAO.updateHospitalProfile(hospitalProfile);
    }

    /**
     * Checks whether the current hospital
     * has a profile in Firestore.
     */
    public boolean hospitalProfileExists() {

        return hospitalDAO.hospitalProfileExists();
    }
}