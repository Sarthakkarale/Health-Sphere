package com.healthsphere.dao.admin;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReviewDAO {

    private static final String COLLECTION_NAME = "reviews";

    private final Firestore firestore;

    public ReviewDAO() {

        this.firestore =
                FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    public String createReview(
            Map<String, Object> reviewData) throws Exception {

        if (reviewData == null ||
                reviewData.isEmpty()) {

            throw new IllegalArgumentException(
                    "Review data cannot be empty."
            );
        }

        String reviewId =
                firestore
                        .collection(COLLECTION_NAME)
                        .document()
                        .getId();

        firestore
                .collection(COLLECTION_NAME)
                .document(reviewId)
                .set(reviewData)
                .get();

        return reviewId;
    }

    // =========================================================
    // GET ALL REVIEWS
    // =========================================================

    public List<Map<String, Object>> getAllReviews()
            throws Exception {

        ApiFuture<QuerySnapshot> future =
                firestore
                        .collection(COLLECTION_NAME)
                        .get();

        QuerySnapshot snapshot =
                future.get();

        List<Map<String, Object>> reviews =
                new ArrayList<>();

        for (
                DocumentSnapshot document :
                snapshot.getDocuments()
        ) {

            if (!document.exists()) {
                continue;
            }

            Map<String, Object> data =
                    document.getData();

            if (data == null) {
                continue;
            }

            /*
             * Keep Firestore document ID available
             * to the Controller/View if required.
             */
            data.put(
                    "id",
                    document.getId()
            );

            reviews.add(data);
        }

        return reviews;
    }

    // =========================================================
    // GET REVIEW BY ID
    // =========================================================

    public Map<String, Object> getReviewById(
            String reviewId) throws Exception {

        if (reviewId == null ||
                reviewId.isBlank()) {

            return null;
        }

        DocumentSnapshot document =
                firestore
                        .collection(COLLECTION_NAME)
                        .document(reviewId)
                        .get()
                        .get();

        if (!document.exists()) {
            return null;
        }

        Map<String, Object> data =
                document.getData();

        if (data != null) {

            data.put(
                    "id",
                    document.getId()
            );
        }

        return data;
    }

    // =========================================================
    // GET REVIEWS BY ROLE
    // =========================================================

    public List<Map<String, Object>> getReviewsByRole(
            String role) throws Exception {

        if (role == null ||
                role.isBlank()) {

            return List.of();
        }

        Query query =
                firestore
                        .collection(COLLECTION_NAME)
                        .whereEqualTo(
                                "userRole",
                                role
                        );

        QuerySnapshot snapshot =
                query.get().get();

        List<Map<String, Object>> reviews =
                new ArrayList<>();

        for (
                DocumentSnapshot document :
                snapshot.getDocuments()
        ) {

            if (!document.exists()) {
                continue;
            }

            Map<String, Object> data =
                    document.getData();

            if (data != null) {

                data.put(
                        "id",
                        document.getId()
                );

                reviews.add(data);
            }
        }

        return reviews;
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    public boolean deleteReview(
            String reviewId) throws Exception {

        if (reviewId == null ||
                reviewId.isBlank()) {

            return false;
        }

        firestore
                .collection(COLLECTION_NAME)
                .document(reviewId)
                .delete()
                .get();

        return true;
    }
}