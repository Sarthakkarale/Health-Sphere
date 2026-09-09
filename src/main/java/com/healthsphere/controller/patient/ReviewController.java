package com.healthsphere.controller.patient;

import java.util.List;

import com.healthsphere.dao.patient.ReviewDAO;
import com.healthsphere.model.PatientProfile;
import com.healthsphere.model.Review;
import com.healthsphere.util.SessionManager;

public class ReviewController {

    private final ReviewDAO reviewDAO;
    private final PatientController patientController;

    public ReviewController() {

        this.reviewDAO =
                new ReviewDAO();

        this.patientController =
                new PatientController();
    }

    // =========================================================
    // CREATE REVIEW
    // =========================================================

    public Review createReview(

            String appointmentId,

            String targetType,
            String targetId,
            String targetName,

            int rating,

            String comment) {

        // -----------------------------------------------------
        // VALIDATE SESSION
        // -----------------------------------------------------

        validateSession();

        // -----------------------------------------------------
        // VALIDATE APPOINTMENT
        // -----------------------------------------------------

        if (appointmentId == null ||
                appointmentId.isBlank()) {

            throw new IllegalArgumentException(
                    "Appointment ID is missing."
            );
        }

        // -----------------------------------------------------
        // VALIDATE TARGET TYPE
        // -----------------------------------------------------

        if (targetType == null ||
                targetType.isBlank()) {

            throw new IllegalArgumentException(
                    "Review type is missing."
            );
        }

        if (!targetType.equalsIgnoreCase("DOCTOR")
                &&
                !targetType.equalsIgnoreCase("HOSPITAL")) {

            throw new IllegalArgumentException(
                    "Invalid review type."
            );
        }

        // -----------------------------------------------------
        // VALIDATE TARGET
        // -----------------------------------------------------

        if (targetId == null ||
                targetId.isBlank()) {

            throw new IllegalArgumentException(
                    "Review target is missing."
            );
        }

        if (targetName == null ||
                targetName.isBlank()) {

            throw new IllegalArgumentException(
                    "Review target name is missing."
            );
        }

        // -----------------------------------------------------
        // VALIDATE RATING
        // -----------------------------------------------------

        if (rating < 1 || rating > 5) {

            throw new IllegalArgumentException(
                    "Please select a rating between 1 and 5."
            );
        }

        // -----------------------------------------------------
        // VALIDATE COMMENT
        // -----------------------------------------------------

        if (comment == null ||
                comment.isBlank()) {

            throw new IllegalArgumentException(
                    "Please enter your review."
            );
        }

        // -----------------------------------------------------
        // CURRENT PATIENT
        // -----------------------------------------------------

        String patientUid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        PatientProfile profile =
                patientController
                        .getCurrentPatientProfile();

        String patientName =
                buildPatientName(profile);

        // -----------------------------------------------------
        // CHECK DUPLICATE REVIEW
        // -----------------------------------------------------

        boolean alreadyReviewed =
                reviewDAO.hasReview(

                        patientUid,

                        appointmentId,

                        targetType.toUpperCase(),

                        targetId
                );

        if (alreadyReviewed) {

            throw new IllegalArgumentException(

                    "You have already reviewed this "
                            + targetType.toLowerCase()
                            + " for this appointment."
            );
        }

        // -----------------------------------------------------
        // CREATE REVIEW OBJECT
        // -----------------------------------------------------

        Review review =
                new Review();

        review.setPatientUid(
                patientUid
        );

        review.setPatientName(
                patientName
        );

        review.setAppointmentId(
                appointmentId
        );

        review.setTargetType(
                targetType
                        .trim()
                        .toUpperCase()
        );

        review.setTargetId(
                targetId
                        .trim()
        );

        review.setTargetName(
                targetName
                        .trim()
        );

        review.setRating(
                rating
        );

        review.setComment(
                comment
                        .trim()
        );

        // -----------------------------------------------------
        // SAVE
        // -----------------------------------------------------

        Review created = reviewDAO.createReview(
                review
        );
        if (created != null && targetType != null && targetId != null) {
            String cacheKey = targetType.trim().toUpperCase() + ":" + targetId.trim();
            RATING_TEXT_CACHE.remove(cacheKey);
        }
        return created;
    }

    // =========================================================
    // GET REVIEWS FOR DOCTOR / HOSPITAL
    // =========================================================

    public List<Review> getReviewsByTarget(

            String targetType,
            String targetId) {

        if (targetType == null ||
                targetType.isBlank()) {

            throw new IllegalArgumentException(
                    "Target type is required."
            );
        }

        if (targetId == null ||
                targetId.isBlank()) {

            throw new IllegalArgumentException(
                    "Target ID is required."
            );
        }

        return reviewDAO.getReviewsByTarget(

                targetType
                        .trim()
                        .toUpperCase(),

                targetId
                        .trim()
        );
    }

    // In-memory cache for rating strings to avoid blocking UI rendering with repeated DB calls
    private static final java.util.concurrent.ConcurrentHashMap<String, String> RATING_TEXT_CACHE = new java.util.concurrent.ConcurrentHashMap<>();

    // =========================================================
    // GET AVERAGE RATING & REVIEW COUNT
    // =========================================================

    public double getAverageRating(String targetType, String targetId) {
        if (targetType == null || targetType.isBlank() || targetId == null || targetId.isBlank()) {
            return 0.0;
        }
        return reviewDAO.getAverageRating(targetType.trim().toUpperCase(), targetId.trim());
    }

    public int getReviewCount(String targetType, String targetId) {
        if (targetType == null || targetType.isBlank() || targetId == null || targetId.isBlank()) {
            return 0;
        }
        List<Review> reviews = reviewDAO.getReviewsByTarget(targetType.trim().toUpperCase(), targetId.trim());
        return reviews != null ? reviews.size() : 0;
    }

    public String getFormattedRatingText(String targetType, String targetId) {
        if (targetType == null || targetType.isBlank() || targetId == null || targetId.isBlank()) {
            return "No ratings yet";
        }
        String cacheKey = targetType.trim().toUpperCase() + ":" + targetId.trim();
        if (RATING_TEXT_CACHE.containsKey(cacheKey)) {
            return RATING_TEXT_CACHE.get(cacheKey);
        }
        return calculateAndCacheRatingText(targetType, targetId, cacheKey);
    }

    public void getFormattedRatingTextAsync(String targetType, String targetId, java.util.function.Consumer<String> callback) {
        if (targetType == null || targetType.isBlank() || targetId == null || targetId.isBlank()) {
            if (callback != null) callback.accept("No ratings yet");
            return;
        }
        String cacheKey = targetType.trim().toUpperCase() + ":" + targetId.trim();
        if (RATING_TEXT_CACHE.containsKey(cacheKey)) {
            if (callback != null) callback.accept(RATING_TEXT_CACHE.get(cacheKey));
            return;
        }
        com.healthsphere.util.AppBackgroundExecutor.execute(() -> {
            String formatted = calculateAndCacheRatingText(targetType, targetId, cacheKey);
            if (callback != null) {
                javafx.application.Platform.runLater(() -> callback.accept(formatted));
            }
        });
    }

    private String calculateAndCacheRatingText(String targetType, String targetId, String cacheKey) {
        try {
            int count = getReviewCount(targetType, targetId);
            if (count == 0) {
                String val = "No ratings yet";
                RATING_TEXT_CACHE.put(cacheKey, val);
                return val;
            }
            double avg = getAverageRating(targetType, targetId);
            if (avg <= 0.0) {
                String val = "No ratings yet";
                RATING_TEXT_CACHE.put(cacheKey, val);
                return val;
            }
            String result = String.format("★ %.1f  (%d %s)", avg, count, count == 1 ? "Review" : "Reviews");
            RATING_TEXT_CACHE.put(cacheKey, result);
            return result;
        } catch (Exception e) {
            String val = "No ratings yet";
            RATING_TEXT_CACHE.put(cacheKey, val);
            return val;
        }
    }

    // =========================================================
    // GET CURRENT PATIENT REVIEWS
    // =========================================================

    public List<Review>
            getCurrentPatientReviews() {

        validateSession();

        String patientUid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return reviewDAO.getPatientReviews(
                patientUid
        );
    }

    // =========================================================
    // CHECK IF CURRENT PATIENT ALREADY REVIEWED
    // =========================================================

    public boolean hasCurrentPatientReviewed(

            String appointmentId,

            String targetType,

            String targetId) {

        validateSession();

        if (appointmentId == null ||
                appointmentId.isBlank()) {

            return false;
        }

        if (targetType == null ||
                targetType.isBlank()) {

            return false;
        }

        if (targetId == null ||
                targetId.isBlank()) {

            return false;
        }

        String patientUid =
                SessionManager
                        .getCurrentUser()
                        .getUid();

        return reviewDAO.hasReview(

                patientUid,

                appointmentId,

                targetType
                        .trim()
                        .toUpperCase(),

                targetId
                        .trim()
        );
    }

    // =========================================================
    // BUILD PATIENT NAME
    // =========================================================

    private String buildPatientName(
            PatientProfile profile) {

        if (profile == null) {

            return "Patient";
        }

        String firstName =
                profile.getFirstName() == null
                        ? ""
                        : profile
                                .getFirstName()
                                .trim();

        String lastName =
                profile.getLastName() == null
                        ? ""
                        : profile
                                .getLastName()
                                .trim();

        String fullName =
                (firstName
                        + " "
                        + lastName)
                        .trim();

        return fullName.isBlank()
                ? "Patient"
                : fullName;
    }

    // =========================================================
    // SESSION VALIDATION
    // =========================================================

    private void validateSession() {

        if (!SessionManager.isLoggedIn()) {

            throw new IllegalStateException(
                    "No active user session."
            );
        }

        if (SessionManager
                .getCurrentUser() == null ||

                SessionManager
                        .getCurrentUser()
                        .getUid() == null ||

                SessionManager
                        .getCurrentUser()
                        .getUid()
                        .isBlank()) {

            throw new IllegalStateException(
                    "Current user session is invalid."
            );
        }
    }
}