package com.healthsphere.model;

import java.util.ArrayList;
import java.util.List;

public class HealthAssessment {

    private boolean isHealthRelated;
    private String rejectionMessage;
    private String predictedCondition;
    private String severityLevel; // Mild, Moderate, Severe, Emergency
    private String confidenceScore; // High, Medium, Low
    private List<String> detectedSymptoms;
    private List<String> possibleCauses;
    private String overview;
    private List<String> recommendedActions;
    private List<String> precautions;
    private String whenToSeeDoctor;
    private String disclaimer;

    public HealthAssessment() {
        this.isHealthRelated = true;
        this.detectedSymptoms = new ArrayList<>();
        this.possibleCauses = new ArrayList<>();
        this.recommendedActions = new ArrayList<>();
        this.precautions = new ArrayList<>();
        this.disclaimer = "This AI health assessment is for informational purposes only and does not replace a professional medical diagnosis or emergency service.";
    }

    public static HealthAssessment createNonHealthResponse(String message) {
        HealthAssessment assessment = new HealthAssessment();
        assessment.setHealthRelated(false);
        assessment.setRejectionMessage(message != null ? message : "I am specialized only in health and symptom analysis. Please ask a symptom or medical-related question.");
        return assessment;
    }

    // Getters and Setters

    public boolean isHealthRelated() {
        return isHealthRelated;
    }

    public void setHealthRelated(boolean healthRelated) {
        isHealthRelated = healthRelated;
    }

    public String getRejectionMessage() {
        return rejectionMessage;
    }

    public void setRejectionMessage(String rejectionMessage) {
        this.rejectionMessage = rejectionMessage;
    }

    public String getPredictedCondition() {
        return predictedCondition != null ? predictedCondition : "General Health Consultation";
    }

    public void setPredictedCondition(String predictedCondition) {
        this.predictedCondition = predictedCondition;
    }

    public String getSeverityLevel() {
        return severityLevel != null ? severityLevel : "Moderate";
    }

    public void setSeverityLevel(String severityLevel) {
        this.severityLevel = severityLevel;
    }

    public String getConfidenceScore() {
        return confidenceScore != null ? confidenceScore : "Medium";
    }

    public void setConfidenceScore(String confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public List<String> getDetectedSymptoms() {
        return detectedSymptoms;
    }

    public void setDetectedSymptoms(List<String> detectedSymptoms) {
        this.detectedSymptoms = detectedSymptoms != null ? detectedSymptoms : new ArrayList<>();
    }

    public List<String> getPossibleCauses() {
        return possibleCauses;
    }

    public void setPossibleCauses(List<String> possibleCauses) {
        this.possibleCauses = possibleCauses != null ? possibleCauses : new ArrayList<>();
    }

    public String getOverview() {
        return overview != null ? overview : "";
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(List<String> recommendedActions) {
        this.recommendedActions = recommendedActions != null ? recommendedActions : new ArrayList<>();
    }

    public List<String> getPrecautions() {
        return precautions;
    }

    public void setPrecautions(List<String> precautions) {
        this.precautions = precautions != null ? precautions : new ArrayList<>();
    }

    public String getWhenToSeeDoctor() {
        return whenToSeeDoctor != null ? whenToSeeDoctor : "If symptoms persist for more than 48 hours or worsen significantly, consult a licensed healthcare professional.";
    }

    public void setWhenToSeeDoctor(String whenToSeeDoctor) {
        this.whenToSeeDoctor = whenToSeeDoctor;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
