
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

    // =========================================================
    // GET ALL HOSPITALS
    // =========================================================

    public List<HospitalProfile> getAllHospitals() {

        return hospitalDAO.getAllHospitals();
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