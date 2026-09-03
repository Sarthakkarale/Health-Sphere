package com.healthsphere.controller.admin;

import com.healthsphere.dao.admin.ApplicationReviewDAO;
import com.healthsphere.model.ApplicationReview;

import java.util.List;

public class ApplicationReviewController {

    private final ApplicationReviewDAO applicationReviewDAO;

    public ApplicationReviewController() {
        this.applicationReviewDAO =
                new ApplicationReviewDAO();
    }

    /**
     * Get all application reviews.
     */
    public List<ApplicationReview> getAllReviews() {

        return applicationReviewDAO.getAllReviews();
    }

    /**
     * Get a review by its ID.
     */
    public ApplicationReview getReview(
            String reviewId) {

        validateId(
                reviewId,
                "Review ID"
        );

        return applicationReviewDAO.getReview(
                reviewId
        );
    }

    /**
     * Get reviews for a particular application.
     */
    public List<ApplicationReview> getReviewsByApplicationId(
            String applicationId) {

        validateId(
                applicationId,
                "Application ID"
        );

        return applicationReviewDAO
                .getReviewsByApplicationId(
                        applicationId
                );
    }

    /**
     * Get reviews submitted by a particular user.
     */
    public List<ApplicationReview> getReviewsByApplicantUid(
            String applicantUid) {

        validateId(
                applicantUid,
                "Applicant UID"
        );

        return applicationReviewDAO
                .getReviewsByApplicantUid(
                        applicantUid
                );
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

        return applicationReviewDAO
                .getReviewsByStatus(
                        status
                );
    }

    /**
     * Create a new application review.
     */
    public ApplicationReview createReview(
            ApplicationReview review) {

        if (review == null) {

            throw new IllegalArgumentException(
                    "Application review cannot be null."
            );
        }

        return applicationReviewDAO.createReview(
                review
        );
    }

    /**
     * Update an existing review.
     */
    public boolean updateReview(
            ApplicationReview review) {

        if (review == null) {

            throw new IllegalArgumentException(
                    "Application review cannot be null."
            );
        }

        try {

            applicationReviewDAO.updateReview(
                    review
            );

            return true;

        } catch (RuntimeException e) {

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Approve a review.
     */
    public boolean approveReview(
            String reviewId) {

        return updateStatus(
                reviewId,
                "APPROVED"
        );
    }

    /**
     * Reject a review.
     */
    public boolean rejectReview(
            String reviewId) {

        return updateStatus(
                reviewId,
                "REJECTED"
        );
    }

    /**
     * Set a review back to pending.
     */
    public boolean setReviewPending(
            String reviewId) {

        return updateStatus(
                reviewId,
                "PENDING"
        );
    }

    /**
     * Update review status.
     */
    private boolean updateStatus(
            String reviewId,
            String status) {

        validateId(
                reviewId,
                "Review ID"
        );

        try {

            applicationReviewDAO.updateReviewStatus(
                    reviewId,
                    status
            );

            return true;

        } catch (RuntimeException e) {

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Delete a review.
     */
    public boolean deleteReview(
            String reviewId) {

        validateId(
                reviewId,
                "Review ID"
        );

        try {

            applicationReviewDAO.deleteReview(
                    reviewId
            );

            return true;

        } catch (RuntimeException e) {

            e.printStackTrace();

            return false;
        }
    }

    /**
     * Validate ID.
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