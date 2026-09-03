package com.healthsphere.config;

import com.google.cloud.firestore.Firestore;

public class Firebase {

    public static Firestore db;

    public static synchronized Firestore getDB() {
        if (db == null) {
            try {
                FirebaseConfig.initialize();
                db = FirebaseConfig.getFirestore();
            } catch (Exception e) {
                System.out.println("Firebase initialization in Firebase.getDB(): " + e.getMessage());
            }
        }
        return db;
    }
}
