package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.DepartmentDAO;
import com.healthsphere.model.HospitalDepartment;

import java.util.List;

public class DepartmentController {

    private final DepartmentDAO departmentDAO;

    public DepartmentController() {
        this.departmentDAO = new DepartmentDAO();
    }

    /**
     * Add a new department.
     */
    public String addDepartment(
            String name,
            String headDoctorId,
            String category,
            boolean is24x7) {

        validateDepartmentInput(
                name,
                category
        );

        String normalizedName =
                name.trim();

        String normalizedCategory =
                category.trim();

        String normalizedHeadDoctorId =
                normalizeOptionalValue(
                        headDoctorId
                );

        /*
         * Prevent duplicate active department names
         * within the same hospital.
         */
        if (departmentDAO.departmentExistsByName(
                normalizedName)) {

            throw new IllegalArgumentException(
                    "A department with this name already exists."
            );
        }

        HospitalDepartment department =
                new HospitalDepartment();

        department.setName(
                normalizedName
        );

        department.setHeadDoctorId(
                normalizedHeadDoctorId
        );

        department.setCategory(
                normalizedCategory
        );

        department.set24x7(
                is24x7
        );

        department.setActive(
                true
        );

        return departmentDAO.createDepartment(
                department
        );
    }

    /**
     * Get all active departments.
     */
    public List<HospitalDepartment>
    getAllDepartments() {

        return departmentDAO.getAllDepartments();
    }

    /**
     * Get one department by ID.
     */
    public HospitalDepartment getDepartmentById(
            String departmentId) {

        validateDepartmentId(
                departmentId
        );

        HospitalDepartment department =
                departmentDAO.getDepartmentById(
                        departmentId.trim()
                );

        if (department == null) {

            throw new IllegalArgumentException(
                    "Department not found."
            );
        }

        return department;
    }

    /**
     * Update an existing department.
     */
    public void updateDepartment(
            String departmentId,
            String name,
            String headDoctorId,
            String category,
            boolean is24x7) {

        validateDepartmentId(
                departmentId
        );

        validateDepartmentInput(
                name,
                category
        );

        String normalizedId =
                departmentId.trim();

        String normalizedName =
                name.trim();

        String normalizedCategory =
                category.trim();

        String normalizedHeadDoctorId =
                normalizeOptionalValue(
                        headDoctorId
                );

        HospitalDepartment existing =
                departmentDAO.getDepartmentById(
                        normalizedId
                );

        if (existing == null) {

            throw new IllegalArgumentException(
                    "Department not found."
            );
        }

        /*
         * Check duplicate name only when
         * the department name is actually changed.
         */
        if (!existing.getName()
                .equalsIgnoreCase(
                        normalizedName
                )
                && departmentDAO
                .departmentExistsByName(
                        normalizedName
                )) {

            throw new IllegalArgumentException(
                    "A department with this name already exists."
            );
        }

        existing.setName(
                normalizedName
        );

        existing.setHeadDoctorId(
                normalizedHeadDoctorId
        );

        existing.setCategory(
                normalizedCategory
        );

        existing.set24x7(
                is24x7
        );

        departmentDAO.updateDepartment(
                existing
        );
    }

    /**
     * Deactivate a department.
     */
    public void removeDepartment(
            String departmentId) {

        validateDepartmentId(
                departmentId
        );

        HospitalDepartment department =
                departmentDAO.getDepartmentById(
                        departmentId.trim()
                );

        if (department == null) {

            throw new IllegalArgumentException(
                    "Department not found."
            );
        }

        departmentDAO.deactivateDepartment(
                departmentId.trim()
        );
    }

    /**
     * Reactivate a department.
     */
    public void reactivateDepartment(
            String departmentId) {

        validateDepartmentId(
                departmentId
        );

        departmentDAO.reactivateDepartment(
                departmentId.trim()
        );
    }

    /**
     * Check department ID.
     */
    public boolean departmentExists(
            String departmentId) {

        if (departmentId == null
                || departmentId.trim().isEmpty()) {

            return false;
        }

        return departmentDAO.departmentExists(
                departmentId.trim()
        );
    }

    /**
     * Check department name.
     */
    public boolean departmentExistsByName(
            String name) {

        if (name == null
                || name.trim().isEmpty()) {

            return false;
        }

        return departmentDAO.departmentExistsByName(
                name.trim()
        );
    }

    /**
     * Validate department input.
     */
    private void validateDepartmentInput(
            String name,
            String category) {

        if (name == null
                || name.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department name is required."
            );
        }

        if (name.trim().length() < 2) {

            throw new IllegalArgumentException(
                    "Department name must contain at least 2 characters."
            );
        }

        if (category == null
                || category.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department category is required."
            );
        }
    }

    /**
     * Validate department ID.
     */
    private void validateDepartmentId(
            String departmentId) {

        if (departmentId == null
                || departmentId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department ID is required."
            );
        }
    }

    /**
     * Normalize optional fields.
     */
    private String normalizeOptionalValue(
            String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return null;
        }

        return value.trim();
    }
}