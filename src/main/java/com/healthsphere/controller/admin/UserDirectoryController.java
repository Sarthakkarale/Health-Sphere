package com.healthsphere.controller.admin;

import com.healthsphere.dao.authentication.UserDAO;
import com.healthsphere.model.UserProfile;

import java.util.List;

public class UserDirectoryController {

    private final UserDAO userDAO;

    public UserDirectoryController() {
        this.userDAO = new UserDAO();
    }

    // ============================================================
    // GET ALL USERS
    // ============================================================

    public List<UserProfile> getAllUsers() {

        return userDAO.getAllUserProfiles();
    }

    // ============================================================
    // GET USER BY UID
    // ============================================================

    public UserProfile getUserByUid(
            String uid) {

        validateId(
                uid,
                "User UID"
        );

        return userDAO.getUserProfile(
                uid
        );
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

        return userDAO.getUsersByRole(
                role
        );
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

        return userDAO.getUsersByStatus(
                status
        );
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

        return userDAO.searchUsersByEmail(
                email
        );
    }

    // ============================================================
    // UPDATE USER STATUS
    // ============================================================

    public boolean updateUserStatus(
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

            userDAO.updateUserStatus(
                    uid,
                    status
            );

            return true;

        } catch (RuntimeException e) {

            e.printStackTrace();

            return false;
        }
    }

    // ============================================================
    // UPDATE USER PROFILE
    // ============================================================

    public boolean updateUserProfile(
            UserProfile userProfile) {

        if (userProfile == null) {

            throw new IllegalArgumentException(
                    "User profile cannot be null."
            );
        }

        try {

            userDAO.updateUserProfile(
                    userProfile
            );

            return true;

        } catch (RuntimeException e) {

            e.printStackTrace();

            return false;
        }
    }

    // ============================================================
    // DELETE USER
    // ============================================================

    public boolean deleteUser(
            String uid) {

        validateId(
                uid,
                "User UID"
        );

        try {

            userDAO.deleteUserProfile(
                    uid
            );

            return true;

        } catch (RuntimeException e) {

            e.printStackTrace();

            return false;
        }
    }

    // ============================================================
    // ACTIVATE USER
    // ============================================================

    public boolean activateUser(
            String uid) {

        return updateUserStatus(
                uid,
                "ACTIVE"
        );
    }

    // ============================================================
    // DEACTIVATE USER
    // ============================================================

    public boolean deactivateUser(
            String uid) {

        return updateUserStatus(
                uid,
                "INACTIVE"
        );
    }

    // ============================================================
    // SUSPEND USER
    // ============================================================

    public boolean suspendUser(
            String uid) {

        return updateUserStatus(
                uid,
                "SUSPENDED"
        );
    }

    // ============================================================
    // VALIDATION
    // ============================================================

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