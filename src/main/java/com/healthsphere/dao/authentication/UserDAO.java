package com.healthsphere.dao.authentication;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.UserProfile;

import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    private final Firestore db;

    public UserDAO() {
        this.db = FirebaseConfig.getFirestore();
    }

    // ============================================================
    // CREATE USER PROFILE
    // ============================================================

    public void createUserProfile(UserProfile userProfile) {

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

            db.collection("users")
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

    public UserProfile getUserProfile(String uid) {

        try {

            DocumentSnapshot document =
                    db.collection("users")
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
}