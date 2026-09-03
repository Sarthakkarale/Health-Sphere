package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.ReviewDAO;

import java.util.List;
import java.util.Map;

public class ReviewController {

    private final ReviewDAO reviewDAO;

    public ReviewController() {

        this.reviewDAO =
                new ReviewDAO();
    }

    // =========================================================
    // GET ALL REVIEWS
    // =========================================================

    public List<Map<String, Object>> getAllReviews() {

        try {

            return reviewDAO
                    .getAllReviews();

        } catch (Exception e) {

            System.err.println(
                    "Failed to load reviews: "
                            + e.getMessage()
            );

            e.printStackTrace();

            return List.of();
        }
    }

    // =========================================================
    // GET REVIEW BY ID
    // =========================================================

    public Map<String, Object> getReviewById(
            String reviewId) {

        try {

            if (reviewId == null ||
                    reviewId.isBlank()) {

                return null;
            }

            return reviewDAO
                    .getReviewById(
                            reviewId
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to get review: "
                            + e.getMessage()
            );

            return null;
        }
    }

    // =========================================================
    // GET REVIEWS BY ROLE
    // =========================================================

    public List<Map<String, Object>>
    getReviewsByRole(String role) {

        try {

            if (role == null ||
                    role.isBlank()) {

                return List.of();
            }

            return reviewDAO
                    .getReviewsByRole(
                            role
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to load reviews by role: "
                            + e.getMessage()
            );

            return List.of();
        }
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    public String createReview(
            Map<String, Object> reviewData) {

        try {

            if (reviewData == null ||
                    reviewData.isEmpty()) {

                return null;
            }

            return reviewDAO
                    .createReview(
                            reviewData
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to create review: "
                            + e.getMessage()
            );

            return null;
        }
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    public boolean deleteReview(
            String reviewId) {

        try {

            if (reviewId == null ||
                    reviewId.isBlank()) {

                return false;
            }

            return reviewDAO
                    .deleteReview(
                            reviewId
                    );

        } catch (Exception e) {

            System.err.println(
                    "Failed to delete review: "
                            + e.getMessage()
            );

            return false;
        }
    }
}