package com.healthsphere.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {

    private static Firestore firestore;

    public static void initialize() throws IOException {

        if (!FirebaseApp.getApps().isEmpty()) {
            firestore = FirestoreClient.getFirestore();
            return;
        }

        FileInputStream serviceAccount =
                new FileInputStream("firebase/serviceAccountKey.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        GoogleCredentials.fromStream(serviceAccount)
                )
                .build();

        FirebaseApp.initializeApp(options);

        firestore = FirestoreClient.getFirestore();

        serviceAccount.close();
    }

    public static Firestore getFirestore() {

        if (firestore == null) {
            throw new IllegalStateException(
                    "Firebase has not been initialized. Call FirebaseConfig.initialize() first."
            );
        }

        return firestore;
    }
}