package com.healthsphere.dao.admin;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.model.ApplicationReview;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApplicationReviewDAO {

    private static final String COLLECTION_NAME =
            "application_reviews";

    private final Firestore firestore;

    public ApplicationReviewDAO() {
        this.firestore =
                FirebaseConfig.getFirestore();
    }

    /**
     * Create a new application review.
     */
    public ApplicationReview createReview(
            ApplicationReview review) {

        validateReview(review);

        try {

            if (review.getReviewId() == null ||
                    review.getReviewId().trim().isEmpty()) {

                review.setReviewId(
                        UUID.randomUUID().toString()
                );
            }

            firestore
                    .collection(COLLECTION_NAME)
                    .document(review.getReviewId())
                    .set(review)
                    .get();

            return review;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to create application review.",
                    e
            );
        }
    }

    /**
     * Get a review by review ID.
     */
    public ApplicationReview getReview(
            String reviewId) {

        validateId(
                reviewId,
                "Review ID"
        );

        try {

            DocumentSnapshot document =
                    firestore
                            .collection(COLLECTION_NAME)
                            .document(reviewId)
                            .get()
                            .get();

            if (!document.exists()) {
                return null;
            }

            return document.toObject(
                    ApplicationReview.class
            );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve application review.",
                    e
            );
        }
    }

    /**
     * Get all application reviews.
     */
    public List<ApplicationReview> getAllReviews() {

        try {

            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .get()
                            .get();

            List<ApplicationReview> reviews =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                ApplicationReview review =
                        document.toObject(
                                ApplicationReview.class
                        );

                if (review != null) {
                    reviews.add(review);
                }
            }

            return reviews;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve application reviews.",
                    e
            );
        }
    }

    /**
     * Get reviews for a specific application.
     */
    public List<ApplicationReview> getReviewsByApplicationId(
            String applicationId) {

        validateId(
                applicationId,
                "Application ID"
        );

        try {

            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "applicationId",
                                    applicationId
                            )
                            .get()
                            .get();

            List<ApplicationReview> reviews =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                ApplicationReview review =
                        document.toObject(
                                ApplicationReview.class
                        );

                if (review != null) {
                    reviews.add(review);
                }
            }

            return reviews;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve reviews for application.",
                    e
            );
        }
    }

    /**
     * Get reviews submitted by a specific user.
     */
    public List<ApplicationReview> getReviewsByApplicantUid(
            String applicantUid) {

        validateId(
                applicantUid,
                "Applicant UID"
        );

        try {

            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "applicantUid",
                                    applicantUid
                            )
                            .get()
                            .get();

            List<ApplicationReview> reviews =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                ApplicationReview review =
                        document.toObject(
                                ApplicationReview.class
                        );

                if (review != null) {
                    reviews.add(review);
                }
            }

            return reviews;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve applicant reviews.",
                    e
            );
        }
    }

    /**
     * Get reviews by status.
     */
    public List<ApplicationReview> getReviewsByStatus(
            String status) {

        if (status == null ||
                status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Review status is required."
            );
        }

        try {

            QuerySnapshot snapshot =
                    firestore
                            .collection(COLLECTION_NAME)
                            .whereEqualTo(
                                    "status",
                                    status.trim().toUpperCase()
                            )
                            .get()
                            .get();

            List<ApplicationReview> reviews =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                if (!document.exists()) {
                    continue;
                }

                ApplicationReview review =
                        document.toObject(
                                ApplicationReview.class
                        );

                if (review != null) {
                    reviews.add(review);
                }
            }

            return reviews;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to retrieve reviews by status.",
                    e
            );
        }
    }

    /**
     * Update an existing review.
     */
    public void updateReview(
            ApplicationReview review) {

        validateReview(review);

        validateId(
                review.getReviewId(),
                "Review ID"
        );

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(review.getReviewId())
                    .set(review)
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update application review.",
                    e
            );
        }
    }

    /**
     * Update only the review status.
     */
    public void updateReviewStatus(
            String reviewId,
            String status) {

        validateId(
                reviewId,
                "Review ID"
        );

        if (status == null ||
                status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Review status is required."
            );
        }

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(reviewId)
                    .update(
                            "status",
                            status.trim().toUpperCase()
                    )
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to update application review status.",
                    e
            );
        }
    }

    /**
     * Delete a review.
     */
    public void deleteReview(
            String reviewId) {

        validateId(
                reviewId,
                "Review ID"
        );

        try {

            firestore
                    .collection(COLLECTION_NAME)
                    .document(reviewId)
                    .delete()
                    .get();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to delete application review.",
                    e
            );
        }
    }

    /**
     * Validate review object.
     */
    private void validateReview(
            ApplicationReview review) {

        if (review == null) {

            throw new IllegalArgumentException(
                    "Application review cannot be null."
            );
        }

        if (review.getApplicationId() == null ||
                review.getApplicationId()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if (review.getApplicantUid() == null ||
                review.getApplicantUid()
                        .trim()
                        .isEmpty()) {

            throw new IllegalArgumentException(
                    "Applicant UID is required."
            );
        }

        if (review.getRating() < 1 ||
                review.getRating() > 5) {

            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5."
            );
        }
    }

    /**
     * Validate an ID value.
     */
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