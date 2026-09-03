package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.WardDAO;
import com.healthsphere.model.HospitalWard;

import java.util.List;

public class WardController {

    private final WardDAO wardDAO;

    public WardController() {
        this.wardDAO = new WardDAO();
    }

    // =========================================================
    // CREATE WARD
    // =========================================================

    public String createWard(
            String name,
            String category,
            int capacity,
            boolean is24x7) {

        validateWardInput(
                name,
                category,
                capacity
        );

        HospitalWard ward =
                new HospitalWard();

        ward.setName(
                name.trim()
        );

        ward.setCategory(
                category.trim()
        );

        ward.setCapacity(
                capacity
        );

        ward.set24x7(
                is24x7
        );

        ward.setActive(
                true
        );

        return wardDAO.createWard(
                ward
        );
    }

    // =========================================================
    // GET ALL WARDS
    // =========================================================

    public List<HospitalWard> getAllWards() {

        return wardDAO.getAllWards();
    }

    // =========================================================
    // GET WARD BY ID
    // =========================================================

    public HospitalWard getWardById(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID cannot be empty."
            );
        }

        return wardDAO.getWardById(
                wardId.trim()
        );
    }

    // =========================================================
    // UPDATE WARD
    // =========================================================

    public void updateWard(
            String wardId,
            String name,
            String category,
            int capacity,
            boolean is24x7) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required."
            );
        }

        validateWardInput(
                name,
                category,
                capacity
        );

        HospitalWard ward =
                wardDAO.getWardById(
                        wardId.trim()
                );

        if (ward == null) {

            throw new IllegalArgumentException(
                    "Ward not found."
            );
        }

        ward.setName(
                name.trim()
        );

        ward.setCategory(
                category.trim()
        );

        ward.setCapacity(
                capacity
        );

        ward.set24x7(
                is24x7
        );

        /*
         * DAO method is void in your current project.
         */
        wardDAO.updateWard(
                ward
        );
    }

    // =========================================================
    // DEACTIVATE WARD
    // =========================================================

    public void deactivateWard(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward ID is required."
            );
        }

        /*
         * DAO method is void in your current project.
         */
        wardDAO.deactivateWard(
                wardId.trim()
        );
    }

    // =========================================================
    // CHECK WARD EXISTS
    // =========================================================

    public boolean wardExists(
            String wardId) {

        if (wardId == null
                || wardId.trim().isEmpty()) {

            return false;
        }

        return wardDAO.wardExists(
                wardId.trim()
        );
    }

    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateWardInput(
            String name,
            String category,
            int capacity) {

        if (name == null
                || name.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward name is required."
            );
        }

        if (category == null
                || category.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Ward category is required."
            );
        }

        if (capacity <= 0) {

            throw new IllegalArgumentException(
                    "Ward capacity must be greater than zero."
            );
        }
    }
}