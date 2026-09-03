

package com.healthsphere.dao.patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.healthsphere.config.FirebaseConfig;
import com.healthsphere.exceptions.DatabaseException;
import com.healthsphere.model.Review;

public class ReviewDAO {

    private final Firestore db;

    public ReviewDAO() {

        this.db =
                FirebaseConfig.getFirestore();
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    public Review createReview(
            Review review) {

        try {

            if (review == null) {

                throw new IllegalArgumentException(
                        "Review cannot be null."
                );
            }

            // -------------------------------------------------
            // GENERATE REVIEW ID
            // -------------------------------------------------

            if (review.getReviewId() == null ||
                    review.getReviewId().isBlank()) {

                review.setReviewId(
                        UUID.randomUUID()
                                .toString()
                );
            }

            // -------------------------------------------------
            // CREATED TIME
            // -------------------------------------------------

            if (review.getCreatedAt() == null ||
                    review.getCreatedAt().isBlank()) {

                review.setCreatedAt(
                        LocalDateTime.now()
                                .toString()
                );
            }

            // -------------------------------------------------
            // SAVE TO FIRESTORE
            // -------------------------------------------------

            db.collection("reviews")
                    .document(
                            review.getReviewId()
                    )
                    .set(review)
                    .get();

            System.out.println(
                    "Review created successfully."
            );

            return review;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to create review.",
                    e
            );
        }
    }

    // =========================================================
    // GET REVIEWS BY TARGET
    // =========================================================

    public List<Review> getReviewsByTarget(
            String targetType,
            String targetId) {

        try {

            ApiFuture<QuerySnapshot> future =

                    db.collection("reviews")

                            .whereEqualTo(
                                    "targetType",
                                    targetType
                            )

                            .whereEqualTo(
                                    "targetId",
                                    targetId
                            )

                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<Review> reviews =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Review review =
                        document.toObject(
                                Review.class
                        );

                if (review != null) {

                    if (review.getReviewId() == null ||
                            review.getReviewId().isBlank()) {

                        review.setReviewId(
                                document.getId()
                        );
                    }

                    reviews.add(review);
                }
            }

            return reviews;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve reviews.",
                    e
            );
        }
    }

    // =========================================================
    // GET AVERAGE RATING BY TARGET
    // =========================================================

    public double getAverageRating(
            String targetType,
            String targetId) {

        try {

            if (targetType == null ||
                    targetType.isBlank()) {

                return 0.0;
            }

            if (targetId == null ||
                    targetId.isBlank()) {

                return 0.0;
            }

            List<Review> reviews =
                    getReviewsByTarget(
                            targetType.trim().toUpperCase(),
                            targetId.trim()
                    );

            if (reviews == null ||
                    reviews.isEmpty()) {

                return 0.0;
            }

            int totalRating = 0;
            int validRatings = 0;

            for (Review review : reviews) {

                if (review == null) {
                    continue;
                }

                int rating =
                        review.getRating();

                if (rating >= 1 &&
                        rating <= 5) {

                    totalRating += rating;
                    validRatings++;
                }
            }

            if (validRatings == 0) {
                return 0.0;
            }

            return (double) totalRating /
                    validRatings;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to calculate average rating.",
                    e
            );
        }
    }

    // =========================================================
    // GET PATIENT REVIEWS
    // =========================================================

    public List<Review> getPatientReviews(
            String patientUid) {

        try {

            ApiFuture<QuerySnapshot> future =

                    db.collection("reviews")

                            .whereEqualTo(
                                    "patientUid",
                                    patientUid
                            )

                            .get();

            QuerySnapshot snapshot =
                    future.get();

            List<Review> reviews =
                    new ArrayList<>();

            for (DocumentSnapshot document :
                    snapshot.getDocuments()) {

                Review review =
                        document.toObject(
                                Review.class
                        );

                if (review != null) {

                    if (review.getReviewId() == null ||
                            review.getReviewId().isBlank()) {

                        review.setReviewId(
                                document.getId()
                        );
                    }

                    reviews.add(review);
                }
            }

            return reviews;

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to retrieve patient reviews.",
                    e
            );
        }
    }

    // =========================================================
    // CHECK IF REVIEW ALREADY EXISTS
    // =========================================================

    public boolean hasReview(
            String patientUid,
            String appointmentId,
            String targetType,
            String targetId) {

        try {

            ApiFuture<QuerySnapshot> future =

                    db.collection("reviews")

                            .whereEqualTo(
                                    "patientUid",
                                    patientUid
                            )

                            .whereEqualTo(
                                    "appointmentId",
                                    appointmentId
                            )

                            .whereEqualTo(
                                    "targetType",
                                    targetType
                            )

                            .whereEqualTo(
                                    "targetId",
                                    targetId
                            )

                            .get();

            QuerySnapshot snapshot =
                    future.get();

            return !snapshot.isEmpty();

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to check existing review.",
                    e
            );
        }
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    public void deleteReview(
            String reviewId) {

        try {

            if (reviewId == null ||
                    reviewId.isBlank()) {

                throw new IllegalArgumentException(
                        "Review ID is required."
                );
            }

            db.collection("reviews")
                    .document(reviewId)
                    .delete()
                    .get();

            System.out.println(
                    "Review deleted successfully."
            );

        } catch (Exception e) {

            throw new DatabaseException(
                    "Unable to delete review.",
                    e
            );
        }
    }
}

