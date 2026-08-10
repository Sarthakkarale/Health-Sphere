package com.healthsphere.dao.authentication;

import com.healthsphere.model.AccountStatus;
import com.healthsphere.model.User;

public class UserDAO {

    public void saveUser(User user) {
        // Firestore implementation later.
    }

    public User getUserById(String userId) {
        // Firestore implementation later.
        return null;
    }

    public void updateUser(User user) {
        // Firestore implementation later.
    }

    public void updateAccountStatus(
            String userId,
            AccountStatus status) {

        // Firestore implementation later.
    }
}