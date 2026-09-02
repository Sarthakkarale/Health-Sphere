package com.healthsphere.dao.hospital;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.HospitalDepartment;
import com.healthsphere.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DepartmentDAO {

    private static final String COLLECTION = "hospitalDepartments";

    private final Firestore firestore;

    public DepartmentDAO() {
        this.firestore = FirebaseConfig.getFirestore();
    }

    // ---------------------------------------------------------
    // CURRENT HOSPITAL
    // ---------------------------------------------------------

    private String getCurrentHospitalId() {

        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "No active hospital session."
            );
        }

        String hospitalId =
                SessionManager.getCurrentUser().getUid();

        if (hospitalId == null || hospitalId.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Hospital ID is not available."
            );
        }

        return hospitalId.trim();
    }

    // ---------------------------------------------------------
    // CREATE
    // ---------------------------------------------------------

    public String createDepartment(
            HospitalDepartment department) {

        validateDepartment(department);

        String hospitalId = getCurrentHospitalId();

        try {

            department.setHospitalId(hospitalId);

            // Generate ID if controller did not provide one
            if (department.getDepartmentId() == null
                    || department.getDepartmentId().trim().isEmpty()) {

                department.setDepartmentId(
                        "DEPT-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                                        .toUpperCase()
                );
            }

            department.setDepartmentId(
                    department.getDepartmentId().trim()
            );

            // Make sure it is active when created
            department.setActive(true);

            // Duplicate department name
            if (departmentExistsByName(
                    department.getName())) {

                throw new DatabaseException(
                        "A department with this name already exists."
                );
            }

            firestore.collection(COLLECTION)
                    .document(department.getDepartmentId())
                    .set(department)
                    .get();

            return department.getDepartmentId();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create department.",
                    e
            );
        }
    }

    // ---------------------------------------------------------
    // GET ALL ACTIVE DEPARTMENTS
    // ---------------------------------------------------------

    public List<HospitalDepartment> getAllDepartments() {

        String hospitalId = getCurrentHospitalId();

        try {

            List<HospitalDepartment> departments =
                    new ArrayList<>();

            List<QueryDocumentSnapshot> documents =
                    firestore.collection(COLLECTION)
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

            for (QueryDocumentSnapshot document : documents) {

                HospitalDepartment department =
                        document.toObject(
                                HospitalDepartment.class
                        );

                if (department == null) {
                    continue;
                }

                // Fallback if ID was not stored in document
                if (department.getDepartmentId() == null
                        || department.getDepartmentId()
                        .trim()
                        .isEmpty()) {

                    department.setDepartmentId(
                            document.getId()
                    );
                }

                departments.add(department);
            }

            return departments;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve departments.",
                    e
            );
        }
    }

    // ---------------------------------------------------------
    // GET DEPARTMENT BY ID
    // ---------------------------------------------------------

    public HospitalDepartment getDepartmentById(
            String departmentId) {

        if (departmentId == null
                || departmentId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Department ID cannot be empty."
            );
        }

        String hospitalId = getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore.collection(COLLECTION)
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

            if (department == null) {

                throw new DatabaseException(
                        "Unable to read department."
                );
            }

            // Security check:
            // department must belong to logged-in hospital
            if (!hospitalId.equals(
                    department.getHospitalId())) {

                throw new DatabaseException(
                        "Department does not belong to the current hospital."
                );
            }

            if (department.getDepartmentId() == null
                    || department.getDepartmentId()
                    .trim()
                    .isEmpty()) {

                department.setDepartmentId(
                        document.getId()
                );
            }

            return department;

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve department.",
                    e
            );
        }
    }

    // ---------------------------------------------------------
    // UPDATE
    // ---------------------------------------------------------

    public void updateDepartment(
            HospitalDepartment department) {

        validateDepartment(department);

        if (department.getDepartmentId() == null
                || department.getDepartmentId()
                .trim()
                .isEmpty()) {

            throw new IllegalArgumentException(
                    "Department ID is required."
            );
        }

        String hospitalId = getCurrentHospitalId();

        try {

            HospitalDepartment existing =
                    getDepartmentById(
                            department.getDepartmentId()
                    );

            if (existing == null) {

                throw new DatabaseException(
                        "Department not found."
                );
            }

            // Check duplicate name only when name changed
            if (!existing.getName()
                    .equalsIgnoreCase(
                            department.getName().trim()
                    )) {

                if (departmentExistsByName(
                        department.getName())) {

                    throw new DatabaseException(
                            "A department with this name already exists."
                    );
                }
            }

            department.setDepartmentId(
                    existing.getDepartmentId()
            );

            department.setHospitalId(
                    hospitalId
            );

            // Preserve current active state
            department.setActive(
                    existing.isActive()
            );

            firestore.collection(COLLECTION)
                    .document(existing.getDepartmentId())
                    .set(department)
                    .get();

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update department.",
                    e
            );
        }
    }

    // ---------------------------------------------------------
    // DEACTIVATE / DELETE
    // ---------------------------------------------------------

    public void deactivateDepartment(
            String departmentId) {

        HospitalDepartment department =
                getDepartmentById(departmentId);

        try {

            firestore.collection(COLLECTION)
                    .document(
                            department.getDepartmentId()
                    )
                    .update(
                            "active",
                            false
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to remove department.",
                    e
            );
        }
    }

    // ---------------------------------------------------------
    // REACTIVATE
    // ---------------------------------------------------------

    public void reactivateDepartment(
            String departmentId) {

        HospitalDepartment department =
                getDepartmentById(departmentId);

        try {

            firestore.collection(COLLECTION)
                    .document(
                            department.getDepartmentId()
                    )
                    .update(
                            "active",
                            true
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to reactivate department.",
                    e
            );
        }
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

        String hospitalId = getCurrentHospitalId();

        try {

            DocumentSnapshot document =
                    firestore.collection(COLLECTION)
                            .document(departmentId.trim())
                            .get()
                            .get();

            if (!document.exists()) {
                return false;
            }

            HospitalDepartment department =
                    document.toObject(
                            HospitalDepartment.class
                    );

            if (department == null) {
                return false;
            }

            return hospitalId.equals(
                    department.getHospitalId()
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check department.",
                    e
            );
        }
    }

    // ---------------------------------------------------------
    // EXISTS BY NAME
    // CASE-INSENSITIVE
    // ---------------------------------------------------------

    public boolean departmentExistsByName(
            String name) {

        if (name == null
                || name.trim().isEmpty()) {

            return false;
        }

        String hospitalId = getCurrentHospitalId();

        String normalizedName =
                name.trim();

        try {

            List<QueryDocumentSnapshot> documents =
                    firestore.collection(COLLECTION)
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

            for (QueryDocumentSnapshot document :
                    documents) {

                HospitalDepartment department =
                        document.toObject(
                                HospitalDepartment.class
                        );

                if (department == null
                        || department.getName() == null) {

                    continue;
                }

                if (department.getName()
                        .trim()
                        .equalsIgnoreCase(
                                normalizedName
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

    // ---------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------

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