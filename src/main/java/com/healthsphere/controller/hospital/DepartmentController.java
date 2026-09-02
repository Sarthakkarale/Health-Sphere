package com.healthsphere.controller.hospital;

import com.healthsphere.dao.hospital.DepartmentDAO;
import com.healthsphere.model.HospitalDepartment;

import java.util.List;

public class DepartmentController {

    private final DepartmentDAO departmentDAO;

    public DepartmentController() {
        this.departmentDAO = new DepartmentDAO();
    }

    // ---------------------------------------------------------
    // ADD DEPARTMENT
    // ---------------------------------------------------------

    public String addDepartment(
            String name,
            String headDoctorId,
            String category,
            boolean is24x7) {

        validateName(name);
        validateCategory(category);

        String normalizedName =
                normalize(name);

        String normalizedCategory =
                normalize(category);

        String normalizedDoctorId =
                normalizeOptional(headDoctorId);

        // Check duplicate before creating
        if (departmentDAO.departmentExistsByName(
                normalizedName)) {

            throw new IllegalArgumentException(
                    "A department with this name already exists."
            );
        }

        HospitalDepartment department =
                new HospitalDepartment();

        department.setName(normalizedName);
        department.setHeadDoctorId(
                normalizedDoctorId
        );
        department.setCategory(
                normalizedCategory
        );
        department.set24x7(is24x7);
        department.setActive(true);

        return departmentDAO.createDepartment(
                department
        );
    }

    // ---------------------------------------------------------
    // GET ALL DEPARTMENTS
    // ---------------------------------------------------------

    public List<HospitalDepartment>
    getAllDepartments() {

        return departmentDAO.getAllDepartments();
    }

    // ---------------------------------------------------------
    // GET DEPARTMENT
    // ---------------------------------------------------------

    public HospitalDepartment getDepartmentById(
            String departmentId) {

        validateDepartmentId(departmentId);

        return departmentDAO.getDepartmentById(
                departmentId.trim()
        );
    }

    // ---------------------------------------------------------
    // UPDATE DEPARTMENT
    // ---------------------------------------------------------

    public void updateDepartment(
            String departmentId,
            String name,
            String headDoctorId,
            String category,
            boolean is24x7) {

        validateDepartmentId(departmentId);
        validateName(name);
        validateCategory(category);

        String normalizedId =
                departmentId.trim();

        String normalizedName =
                normalize(name);

        String normalizedCategory =
                normalize(category);

        String normalizedDoctorId =
                normalizeOptional(headDoctorId);

        HospitalDepartment existing =
                departmentDAO.getDepartmentById(
                        normalizedId
                );

        if (existing == null) {

            throw new IllegalArgumentException(
                    "Department not found."
            );
        }

        // Only check duplicate if the name changed
        if (!existing.getName()
                .equalsIgnoreCase(
                        normalizedName
                )) {

            if (departmentDAO.departmentExistsByName(
                    normalizedName)) {

                throw new IllegalArgumentException(
                        "A department with this name already exists."
                );
            }
        }

        existing.setName(
                normalizedName
        );

        existing.setHeadDoctorId(
                normalizedDoctorId
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

    // ---------------------------------------------------------
    // REMOVE / DEACTIVATE
    // ---------------------------------------------------------

    public void removeDepartment(
            String departmentId) {

        validateDepartmentId(departmentId);

        departmentDAO.deactivateDepartment(
                departmentId.trim()
        );
    }

    // ---------------------------------------------------------
    // REACTIVATE
    // ---------------------------------------------------------

    public void reactivateDepartment(
            String departmentId) {

        validateDepartmentId(departmentId);

        departmentDAO.reactivateDepartment(
                departmentId.trim()
        );
    }

    // ---------------------------------------------------------
    // EXISTS BY ID
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // EXISTS BY NAME
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

    private void validateDepartmentId(
            String departmentId) {

        if (departmentId == null
                || departmentId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department ID is required."
            );
        }
    }

    private void validateName(
            String name) {

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
    }

    private void validateCategory(
            String category) {

        if (category == null
                || category.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department category is required."
            );
        }
    }

    private String normalize(
            String value) {

        return value.trim()
                .replaceAll("\\s+", " ");
    }

    private String normalizeOptional(
            String value) {

        if (value == null) {
            return "";
        }

        String normalized =
                value.trim()
                        .replaceAll("\\s+", " ");

        return normalized;
    }
}