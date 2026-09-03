package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO {

    private static final String COLLECTION_NAME = "users";

    private final Firestore db;

    public UserDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE USER PROFILE
    // ============================================================

    public void createUserProfile(
            UserProfile userProfile) {

        validateUserProfile(userProfile);

        try {

            Map<String, Object> userData =
                    new HashMap<>();

            userData.put(
                    "uid",
                    userProfile.getUid()
            );

            userData.put(
                    "email",
                    userProfile.getEmail()
            );

            userData.put(
                    "role",
                    userProfile.getRole()
            );

            userData.put(
                    "status",
                    userProfile.getStatus()
            );

            db.collection(COLLECTION_NAME)
                    .document(userProfile.getUid())
                    .set(userData)
                    .get();

            System.out.println(
                    "User profile created successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create user profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET USER PROFILE
    // ============================================================

    public UserProfile getUserProfile(
            String uid) {

        validateId(
                uid,
                "User UID"
        );

        try {

            DocumentSnapshot document =
                    db.collection(COLLECTION_NAME)
                            .document(uid)
                            .get()
                            .get();

            if (!document.exists()) {

                throw new DatabaseException(
                        "User profile not found."
                );
            }

            return document.toObject(
                    UserProfile.class
            );

        } catch (DatabaseException e) {

            throw e;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve user profile.",
                    e
            );
        }
    }

    // ============================================================
    // GET ALL USER PROFILES
    // ============================================================

    public List<UserProfile> getAllUserProfiles() {

        try {

            QuerySnapshot snapshot =
                    db.collection(COLLECTION_NAME)
                            .get()
                            .get();

            List<UserProfile> users =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                UserProfile user =
                        document.toObject(
                                UserProfile.class
                        );

                if (user != null) {
                    users.add(user);
                }
            }

            return users;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve user profiles.",
                    e
            );
        }
    }

    // ============================================================
    // GET USERS BY ROLE
    // ============================================================

    public List<UserProfile> getUsersByRole(
            String role) {

        validateId(
                role,
                "User role"
        );

        try {

            QuerySnapshot snapshot =
                    db.collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "role",
                                    role.trim()
                            )
                            .get()
                            .get();

            List<UserProfile> users =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                UserProfile user =
                        document.toObject(
                                UserProfile.class
                        );

                if (user != null) {
                    users.add(user);
                }
            }

            return users;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve users by role.",
                    e
            );
        }
    }

    // ============================================================
    // GET USERS BY STATUS
    // ============================================================

    public List<UserProfile> getUsersByStatus(
            String status) {

        validateId(
                status,
                "User status"
        );

        try {

            QuerySnapshot snapshot =
                    db.collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "status",
                                    status.trim()
                            )
                            .get()
                            .get();

            List<UserProfile> users =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                UserProfile user =
                        document.toObject(
                                UserProfile.class
                        );

                if (user != null) {
                    users.add(user);
                }
            }

            return users;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve users by status.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE USER STATUS
    // ============================================================

    public void updateUserStatus(
            String uid,
            String status) {

        validateId(
                uid,
                "User UID"
        );

        validateId(
                status,
                "User status"
        );

        try {

            db.collection(COLLECTION_NAME)
                    .document(uid)
                    .update(
                            "status",
                            status.trim()
                    )
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update user status.",
                    e
            );
        }
    }

    // ============================================================
    // UPDATE COMPLETE USER PROFILE
    // ============================================================

    public void updateUserProfile(
            UserProfile userProfile) {

        validateUserProfile(
                userProfile
        );

        try {

            Map<String, Object> userData =
                    new HashMap<>();

            userData.put(
                    "uid",
                    userProfile.getUid()
            );

            userData.put(
                    "email",
                    userProfile.getEmail()
            );

            userData.put(
                    "role",
                    userProfile.getRole()
            );

            userData.put(
                    "status",
                    userProfile.getStatus()
            );

            db.collection(COLLECTION_NAME)
                    .document(userProfile.getUid())
                    .set(userData)
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to update user profile.",
                    e
            );
        }
    }

    // ============================================================
    // DELETE USER PROFILE
    // ============================================================

    public void deleteUserProfile(
            String uid) {

        validateId(
                uid,
                "User UID"
        );

        try {

            db.collection(COLLECTION_NAME)
                    .document(uid)
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to delete user profile.",
                    e
            );
        }
    }

    // ============================================================
    // SEARCH USER BY EMAIL
    // ============================================================

    public List<UserProfile> searchUsersByEmail(
            String email) {

        validateId(
                email,
                "Email"
        );

        try {

            QuerySnapshot snapshot =
                    db.collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "email",
                                    email.trim()
                            )
                            .get()
                            .get();

            List<UserProfile> users =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                UserProfile user =
                        document.toObject(
                                UserProfile.class
                        );

                if (user != null) {
                    users.add(user);
                }
            }

            return users;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to search users by email.",
                    e
            );
        }
    }

    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateUserProfile(
            UserProfile userProfile) {

        if (userProfile == null) {

            throw new IllegalArgumentException(
                    "User profile cannot be null."
            );
        }

        validateId(
                userProfile.getUid(),
                "User UID"
        );

        validateId(
                userProfile.getEmail(),
                "User email"
        );

        validateId(
                userProfile.getRole(),
                "User role"
        );

        validateId(
                userProfile.getStatus(),
                "User status"
        );
    }

    private void validateId(
            String value,
            String fieldName) {

        if (value == null ||
                value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName + " is required."
            );
        }
    }
}