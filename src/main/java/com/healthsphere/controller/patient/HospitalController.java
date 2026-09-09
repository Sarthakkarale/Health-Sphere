
package com.healthsphere.controller.patient;

import java.util.ArrayList;
import java.util.List;

import com.healthsphere.dao.authentication.HospitalDAO;
import com.healthsphere.model.HospitalProfile;

public class HospitalController {

    private final HospitalDAO hospitalDAO;

    public HospitalController() {

        this.hospitalDAO =
                new HospitalDAO();
    }

    private static volatile List<HospitalProfile> CACHED_HOSPITALS = null;
    private static volatile long LAST_CACHE_TIME = 0;
    private static final long CACHE_TTL_MS = 60_000; // 60 seconds

    public List<HospitalProfile> getAllHospitals() {
        long now = System.currentTimeMillis();
        if (CACHED_HOSPITALS != null && (now - LAST_CACHE_TIME < CACHE_TTL_MS)) {
            return new ArrayList<>(CACHED_HOSPITALS);
        }

        List<HospitalProfile> fresh = hospitalDAO.getAllHospitals();
        if (fresh != null) {
            CACHED_HOSPITALS = new ArrayList<>(fresh);
            LAST_CACHE_TIME = now;
        }
        return fresh != null ? fresh : new ArrayList<>();
    }

    public static void clearCache() {
        CACHED_HOSPITALS = null;
        LAST_CACHE_TIME = 0;
    }

    // =========================================================
    // SEARCH HOSPITALS
    // =========================================================

    public List<HospitalProfile> searchHospitals(
            String searchText) {

        List<HospitalProfile> hospitals =
                hospitalDAO.getAllHospitals();

        if (searchText == null ||
                searchText.trim().isEmpty()) {

            return hospitals;
        }

        String search =
                searchText
                        .trim()
                        .toLowerCase();

        List<HospitalProfile> filtered =
                new ArrayList<>();

        for (HospitalProfile hospital : hospitals) {

            if (hospital == null) {
                continue;
            }

            String hospitalName =
                    safe(hospital.getHospitalName());

            String address =
                    safe(hospital.getAddress());

            String hospitalType =
                    safe(hospital.getHospitalType());

            String contact =
                    safe(hospital.getContact());

            // -------------------------------------------------
            // Search by:
            //
            // Hospital Name
            // Address / Location
            // Hospital Type
            // Contact
            // -------------------------------------------------

            if (hospitalName.contains(search) ||
                    address.contains(search) ||
                    hospitalType.contains(search) ||
                    contact.contains(search)) {

                filtered.add(hospital);
            }
        }

        return filtered;
    }

    // =========================================================
    // SAFE STRING
    // =========================================================

    private String safe(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase();
    }
}