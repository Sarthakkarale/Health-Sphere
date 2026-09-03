package com.healthsphere.dao.hospital;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DepartmentDAO {

    private final Firestore db;

    private static final String COLLECTION =
            "hospitalDepartments";

    public DepartmentDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    /**
     * Creates a new department for the currently
     * logged-in hospital.
     */
    public String createDepartment(
            HospitalDepartment department) {

        try {

            validateDepartment(department);

            String hospitalId = getCurrentHospitalId();

            department.setHospitalId(hospitalId);

            String departmentId =
                    department.getDepartmentId();

            if (departmentId == null
                    || departmentId.trim().isEmpty()) {

                departmentId =
                        "DEPT-" +
                        UUID.randomUUID()
                                .toString()
                                .substring(0, 8)
                                .toUpperCase();

                department.setDepartmentId(
                        departmentId
                );
            }

            department.setActive(true);

            db.collection(COLLECTION)
                    .document(departmentId)
                    .set(department)
                    .get();

            System.out.println(
                    "Department created successfully: "
                            + departmentId
            );

            return departmentId;

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new DatabaseException(
                    "Unable to create department.",
                    e
            );
        }
    }

    /**
     * Returns all active departments belonging
     * to the currently logged-in hospital.
     */
    public List<HospitalDepartment>
    getAllDepartments() {

        try {

            String hospitalId =
                    getCurrentHospitalId();

            List<HospitalDepartment> departments =
                    new ArrayList<>();

            List<QueryDocumentSnapshot> documents =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .whereEqualTo(
                                    "active",
                                    true
                            )
                            .get()
                            .get()
                            .getDocuments();

            for (QueryDocumentSnapshot document
                    : documents) {

                HospitalDepartment department =
                        document.toObject(
                                HospitalDepartment.class
                        );

                departments.add(department);
            }

            return departments;

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new DatabaseException(
                    "Unable to retrieve departments.",
                    e
            );
        }
    }

    /**
     * Returns one department only if it belongs
     * to the currently logged-in hospital.
     */
    public HospitalDepartment getDepartmentById(
            String departmentId) {

        try {

            if (departmentId == null
                    || departmentId.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Department ID is required."
                );
            }

            String hospitalId =
                    getCurrentHospitalId();

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(departmentId.trim())
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            HospitalDepartment department =
                    document.toObject(
                            HospitalDepartment.class
                    );

            if (department == null) {
                return null;
            }

            /*
             * Security check:
             * Never return another hospital's department.
             */
            if (!hospitalId.equals(
                    department.getHospitalId())) {

                return null;
            }

            return department;

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new DatabaseException(
                    "Unable to retrieve department.",
                    e
            );
        }
    }

    /**
     * Updates an existing department.
     */
    public void updateDepartment(
            HospitalDepartment department) {

        try {

            validateDepartment(department);

            if (department.getDepartmentId() == null
                    || department.getDepartmentId()
                    .trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Department ID is required."
                );
            }

            String hospitalId =
                    getCurrentHospitalId();

            HospitalDepartment existing =
                    getDepartmentById(
                            department.getDepartmentId()
                    );

            if (existing == null) {

                throw new DatabaseException(
                        "Department not found."
                );
            }

            /*
             * Preserve ownership and ID.
             */
            department.setDepartmentId(
                    existing.getDepartmentId()
            );

            department.setHospitalId(
                    hospitalId
            );

            department.setActive(
                    existing.isActive()
            );

            db.collection(COLLECTION)
                    .document(
                            existing.getDepartmentId()
                    )
                    .set(department)
                    .get();

            System.out.println(
                    "Department updated successfully."
            );

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new DatabaseException(
                    "Unable to update department.",
                    e
            );
        }
    }

    /**
     * Soft deletes a department.
     *
     * The Firestore document remains present,
     * but active becomes false.
     */
    public void deactivateDepartment(
            String departmentId) {

        try {

            HospitalDepartment department =
                    getDepartmentById(
                            departmentId
                    );

            if (department == null) {

                throw new DatabaseException(
                        "Department not found."
                );
            }

            db.collection(COLLECTION)
                    .document(departmentId.trim())
                    .update(
                            "active",
                            false
                    )
                    .get();

            System.out.println(
                    "Department deactivated successfully."
            );

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new DatabaseException(
                    "Unable to deactivate department.",
                    e
            );
        }
    }

    /**
     * Reactivates a previously deactivated department.
     */
    public void reactivateDepartment(
            String departmentId) {

        try {

            if (departmentId == null
                    || departmentId.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Department ID is required."
                );
            }

            String hospitalId =
                    getCurrentHospitalId();

            DocumentSnapshot document =
                    db.collection(COLLECTION)
                            .document(departmentId.trim())
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "Department not found."
                );
            }

            HospitalDepartment department =
                    document.toObject(
                            HospitalDepartment.class
                    );

            if (department == null
                    || !hospitalId.equals(
                            department.getHospitalId())) {

                throw new DatabaseException(
                        "Department not found."
                );
            }

            db.collection(COLLECTION)
                    .document(departmentId.trim())
                    .update(
                            "active",
                            true
                    )
                    .get();

            System.out.println(
                    "Department reactivated successfully."
            );

        } catch (IllegalArgumentException e) {

            throw e;

        } catch (Exception e) {

            if (e instanceof DatabaseException) {
                throw (DatabaseException) e;
            }

            throw new DatabaseException(
                    "Unable to reactivate department.",
                    e
            );
        }
    }

    /**
     * Checks whether an active department exists
     * by ID for the current hospital.
     */
    public boolean departmentExists(
            String departmentId) {

        if (departmentId == null
                || departmentId.trim().isEmpty()) {

            return false;
        }

        return getDepartmentById(
                departmentId.trim()
        ) != null;
    }

    /**
     * Checks whether an active department with
     * the same name exists in the current hospital.
     *
     * Firestore string equality is case-sensitive,
     * so the final comparison is done in Java.
     */
    public boolean departmentExistsByName(
            String name) {

        if (name == null
                || name.trim().isEmpty()) {

            return false;
        }

        try {

            String hospitalId =
                    getCurrentHospitalId();

            List<QueryDocumentSnapshot> documents =
                    db.collection(COLLECTION)
                            .whereEqualTo(
                                    "hospitalId",
                                    hospitalId
                            )
                            .whereEqualTo(
                                    "active",
                                    true
                            )
                            .get()
                            .get()
                            .getDocuments();

            for (QueryDocumentSnapshot document
                    : documents) {

                HospitalDepartment department =
                        document.toObject(
                                HospitalDepartment.class
                        );

                if (department.getName() != null
                        && department.getName()
                        .trim()
                        .equalsIgnoreCase(
                                name.trim()
                        )) {

                    return true;
                }
            }

            return false;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check department name.",
                    e
            );
        }
    }

    /**
     * Gets the currently logged-in hospital UID.
     */
    private String getCurrentHospitalId() {

        if (!SessionManager.isLoggedIn()) {

            throw new DatabaseException(
                    "No active hospital session."
            );
        }

        String hospitalId =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        if (hospitalId == null
                || hospitalId.trim().isEmpty()) {

            throw new DatabaseException(
                    "Hospital ID is missing from session."
            );
        }

        return hospitalId.trim();
    }

    /**
     * Basic DAO-level validation.
     */
    private void validateDepartment(
            HospitalDepartment department) {

        if (department == null) {

            throw new IllegalArgumentException(
                    "Department cannot be null."
            );
        }

        if (department.getName() == null
                || department.getName()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Department name is required."
            );
        }

        if (department.getCategory() == null
                || department.getCategory()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Department category is required."
            );
        }
    }
}