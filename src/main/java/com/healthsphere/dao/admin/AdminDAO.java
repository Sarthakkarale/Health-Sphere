package com.healthsphere.dao.admin;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;

public class AdminDAO {

    private static final String HOSPITALS_COLLECTION =
            "hospitals";

    private static final String DOCTORS_COLLECTION =
            "doctors";

    private final Firestore firestore;

    public AdminDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    /**
     * Returns the total number of registered hospitals.
     *
     * This method performs the Firestore read required
     * for the Admin Dashboard hospital KPI.
     */
    public int getRegisteredHospitalCount()
            throws Exception {

        QuerySnapshot snapshot =
                firestore
                        .collection(HOSPITALS_COLLECTION)
                        .get()
                        .get();

        return snapshot.size();
    }

    /**
     * Returns the total number of doctors.
     *
     * This method performs the Firestore read required
     * for the Admin Dashboard doctor KPI.
     */
    public int getActiveDoctorCount()
            throws Exception {

        QuerySnapshot snapshot =
                firestore
                        .collection(DOCTORS_COLLECTION)
                        .get()
                        .get();

        return snapshot.size();
    }
}