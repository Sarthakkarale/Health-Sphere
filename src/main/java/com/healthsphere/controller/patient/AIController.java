package com.healthsphere.controller.patient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.healthsphere.model.HealthAssessment;

public class AIController {

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";

    private final HttpClient httpClient;
    private final Gson gson;

    public AIController() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(12))
                .build();
        this.gson = new Gson();
    }

    /**
     * Main method to analyze user symptoms and return a structured HealthAssessment.
     */
    public HealthAssessment analyzeSymptoms(String userPrompt) {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return HealthAssessment.createNonHealthResponse("Please provide a symptom or health concern to analyze.");
        }

        String cleanedPrompt = userPrompt.trim();

        // Check if query is health related
        if (!isHealthRelatedQuery(cleanedPrompt)) {
            return HealthAssessment.createNonHealthResponse(
                    "I am specialized exclusively as an AI Health Assistant for health and symptom analysis. " +
                    "Please ask a medical, wellness, or symptom-related question."
            );
        }

        // Try hitting Groq API
        String apiKey = getGroqApiKey();
        if (apiKey != null && !apiKey.isBlank()) {
            try {
                HealthAssessment groqResult = callGroqApi(cleanedPrompt, apiKey);
                if (groqResult != null) {
                    return sanitizeAssessment(groqResult);
                }
            } catch (Exception e) {
                System.err.println("Groq API call failed: " + e.getMessage() + ". Falling back to local AI analysis engine.");
            }
        }

        // Fallback local intelligent symptom predictor engine
        return sanitizeAssessment(generateLocalHealthAssessment(cleanedPrompt));
    }

    /**
     * Connect to Groq REST API and parse structured JSON response.
     */
    private HealthAssessment callGroqApi(String userPrompt, String apiKey) throws Exception {
        String systemPrompt = 
                "You are an expert AI Health Assistant for HealthSphere.\n" +
                "Your task is to analyze user symptoms and return ONLY a valid JSON object matching this exact schema:\n" +
                "{\n" +
                "  \"isHealthRelated\": true,\n" +
                "  \"predictedCondition\": \"Condition Name\",\n" +
                "  \"severityLevel\": \"Mild\" or \"Moderate\" or \"Severe\" or \"Emergency\",\n" +
                "  \"confidenceScore\": \"High\" or \"Medium\" or \"Low\",\n" +
                "  \"detectedSymptoms\": [\"Symptom 1\", \"Symptom 2\"],\n" +
                "  \"possibleCauses\": [\"Cause 1\", \"Cause 2\"],\n" +
                "  \"overview\": \"Clean overview paragraph without any markdown asterisks or hashes.\",\n" +
                "  \"recommendedActions\": [\"Action 1\", \"Action 2\"],\n" +
                "  \"precautions\": [\"Precaution 1\", \"Precaution 2\"],\n" +
                "  \"whenToSeeDoctor\": \"Clean guidance on when to seek professional care.\"\n" +
                "}\n" +
                "CRITICAL RULES:\n" +
                "1. If the prompt is NOT about symptoms, disease, or medical health, set \"isHealthRelated\": false and provide a polite rejection in \"rejectionMessage\".\n" +
                "2. DO NOT use markdown characters (no *, #, _, ~, `, or bullet stars) anywhere in the text fields.\n" +
                "3. Output ONLY valid JSON, no markdown code block backticks.";

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userPrompt);

        JsonArray messages = new JsonArray();
        messages.add(systemMsg);
        messages.add(userMsg);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", DEFAULT_MODEL);
        requestBody.add("messages", messages);
        requestBody.addProperty("temperature", 0.3);
        requestBody.addProperty("max_tokens", 800);

        JsonObject responseFormat = new JsonObject();
        responseFormat.addProperty("type", "json_object");
        requestBody.add("response_format", responseFormat);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Authorization", "Bearer " + apiKey.trim())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            JsonArray choices = jsonResponse.getAsJsonArray("choices");
            if (choices != null && choices.size() > 0) {
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                JsonObject messageObj = firstChoice.getAsJsonObject("message");
                String content = messageObj.get("content").getAsString();
                
                // Parse AI JSON response into HealthAssessment model
                return parseJsonResponse(content);
            }
        } else {
            System.err.println("Groq API returned HTTP " + response.statusCode() + ": " + response.body());
        }

        return null;
    }

    /**
     * Parses the JSON content string from Groq API into a HealthAssessment object.
     */
    private HealthAssessment parseJsonResponse(String jsonContent) {
        try {
            String cleanedJson = jsonContent.trim();
            if (cleanedJson.startsWith("```json")) {
                cleanedJson = cleanedJson.substring(7);
            } else if (cleanedJson.startsWith("```")) {
                cleanedJson = cleanedJson.substring(3);
            }
            if (cleanedJson.endsWith("```")) {
                cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3);
            }
            cleanedJson = cleanedJson.trim();

            JsonObject obj = JsonParser.parseString(cleanedJson).getAsJsonObject();
            
            HealthAssessment assessment = new HealthAssessment();
            if (obj.has("isHealthRelated") && !obj.get("isHealthRelated").getAsBoolean()) {
                assessment.setHealthRelated(false);
                if (obj.has("rejectionMessage")) {
                    assessment.setRejectionMessage(obj.get("rejectionMessage").getAsString());
                }
                return assessment;
            }

            assessment.setHealthRelated(true);
            if (obj.has("predictedCondition")) assessment.setPredictedCondition(obj.get("predictedCondition").getAsString());
            if (obj.has("severityLevel")) assessment.setSeverityLevel(obj.get("severityLevel").getAsString());
            if (obj.has("confidenceScore")) assessment.setConfidenceScore(obj.get("confidenceScore").getAsString());
            if (obj.has("overview")) assessment.setOverview(obj.get("overview").getAsString());
            if (obj.has("whenToSeeDoctor")) assessment.setWhenToSeeDoctor(obj.get("whenToSeeDoctor").getAsString());

            if (obj.has("detectedSymptoms") && obj.get("detectedSymptoms").isJsonArray()) {
                List<String> list = new ArrayList<>();
                obj.getAsJsonArray("detectedSymptoms").forEach(item -> list.add(item.getAsString()));
                assessment.setDetectedSymptoms(list);
            }
            if (obj.has("possibleCauses") && obj.get("possibleCauses").isJsonArray()) {
                List<String> list = new ArrayList<>();
                obj.getAsJsonArray("possibleCauses").forEach(item -> list.add(item.getAsString()));
                assessment.setPossibleCauses(list);
            }
            if (obj.has("recommendedActions") && obj.get("recommendedActions").isJsonArray()) {
                List<String> list = new ArrayList<>();
                obj.getAsJsonArray("recommendedActions").forEach(item -> list.add(item.getAsString()));
                assessment.setRecommendedActions(list);
            }
            if (obj.has("precautions") && obj.get("precautions").isJsonArray()) {
                List<String> list = new ArrayList<>();
                obj.getAsJsonArray("precautions").forEach(item -> list.add(item.getAsString()));
                assessment.setPrecautions(list);
            }

            return assessment;

        } catch (Exception e) {
            System.err.println("Error parsing Groq JSON response: " + e.getMessage());
            return null;
        }
    }

    /**
     * Strict pre-validation: Checks if the query is related to symptoms, body, illness, or medical care.
     */
    private boolean isHealthRelatedQuery(String prompt) {
        String lower = prompt.toLowerCase(Locale.ROOT);

        // Explicit non-health keywords
        String[] nonHealthKeywords = {
            "coding", "java", "python", "javascript", "code", "programming", "math", "calculus",
            "equation", "football", "cricket", "basketball", "movie", "actor", "politics", "president",
            "election", "stock market", "crypto", "bitcoin", "weather", "recipe", "restaurant",
            "capital of", "who is the president", "solve for x"
        };

        for (String keyword : nonHealthKeywords) {
            if (lower.contains(keyword)) {
                return false;
            }
        }

        // Common health/symptom keywords
        String[] healthKeywords = {
            "fever", "headache", "pain", "cough", "cold", "flu", "stomach", "chest", "dizzy",
            "nausea", "vomiting", "throat", "fatigue", "tired", "rash", "skin", "blood", "pressure",
            "heart", "breathing", "breath", "back", "joint", "muscle", "cramps", "swelling", "infection",
            "diarrhea", "constipation", "migraine", "allergy", "sneezing", "symptom", "disease",
            "illness", "doctor", "health", "medicine", "pill", "treatment", "cure", "sick", "hurt",
            "injury", "burn", "ache", "sore", "sprain", "fracture", "bleed", "bleeding"
        };

        for (String keyword : healthKeywords) {
            if (lower.contains(keyword)) {
                return true;
            }
        }

        // If query is short or generic, check if it describes a state of feeling
        return lower.contains("feel") || lower.contains("body") || lower.contains("having") || lower.contains("getting");
    }

    /**
     * Local intelligent symptom prediction fallback engine.
     */
    private HealthAssessment generateLocalHealthAssessment(String prompt) {
        String lower = prompt.toLowerCase(Locale.ROOT);
        HealthAssessment assessment = new HealthAssessment();
        assessment.setHealthRelated(true);

        if (lower.contains("fever") && (lower.contains("chills") || lower.contains("body ache") || lower.contains("cough"))) {
            assessment.setPredictedCondition("Viral Upper Respiratory Infection / Flu");
            assessment.setSeverityLevel("Moderate");
            assessment.setConfidenceScore("High");
            assessment.setDetectedSymptoms(Arrays.asList("Fever", "Body Ache", "Chills", "Cough"));
            assessment.setPossibleCauses(Arrays.asList("Influenza virus", "Rhinovirus infection", "Seasonal viral exposure"));
            assessment.setOverview("Upper respiratory viral infection affecting the throat, sinus passages, and general immune response.");
            assessment.setRecommendedActions(Arrays.asList(
                "Rest adequately and avoid strenuous physical activities",
                "Maintain high fluid intake (water, warm herbal teas, clear broths)",
                "Monitor body temperature regularly"
            ));
            assessment.setPrecautions(Arrays.asList(
                "Use a face mask around family members",
                "Do not take antibiotics without a physician's prescription"
            ));
            assessment.setWhenToSeeDoctor("Seek immediate medical care if fever exceeds 103°F (39.4°C) or lasts over 3 days.");
        } else if (lower.contains("headache") || lower.contains("migraine") || lower.contains("head ache")) {
            assessment.setPredictedCondition("Tension Headache / Migraine Episode");
            assessment.setSeverityLevel("Moderate");
            assessment.setConfidenceScore("High");
            assessment.setDetectedSymptoms(Arrays.asList("Head Pain", "Sensitivity to Light", "Eye Strain"));
            assessment.setPossibleCauses(Arrays.asList("Stress or fatigue", "Dehydration", "Prolonged screen time", "Irregular sleep pattern"));
            assessment.setOverview("Neurological tension or vascular pain causing throbbing or dull head discomfort.");
            assessment.setRecommendedActions(Arrays.asList(
                "Rest in a quiet, dark room away from bright screens",
                "Apply a cool compress across the forehead or neck",
                "Hydrate thoroughly with water"
            ));
            assessment.setPrecautions(Arrays.asList("Avoid excessive caffeine or loud noise environments"));
            assessment.setWhenToSeeDoctor("Consult a neurologist or physician if accompanied by sudden numbness or blurred vision.");
        } else if (lower.contains("stomach") || lower.contains("nausea") || lower.contains("vomit") || lower.contains("diarrhea")) {
            assessment.setPredictedCondition("Acute Gastroenteritis / Digestive Upset");
            assessment.setSeverityLevel("Moderate");
            assessment.setConfidenceScore("Medium");
            assessment.setDetectedSymptoms(Arrays.asList("Stomach Discomfort", "Nausea", "Digestive Distress"));
            assessment.setPossibleCauses(Arrays.asList("Dietary indiscretion or food sensitivity", "Mild viral gastroenteritis", "Indigestion"));
            assessment.setOverview("Inflammation or irritation of the stomach and intestinal lining.");
            assessment.setRecommendedActions(Arrays.asList(
                "Follow a light BRAT diet (Bananas, Rice, Applesauce, Toast)",
                "Sip oral rehydration fluids to maintain electrolyte balance"
            ));
            assessment.setPrecautions(Arrays.asList("Avoid oily, spicy, dairy, or heavy meals for 24-48 hours"));
            assessment.setWhenToSeeDoctor("Consult a doctor if severe vomiting prevents fluid retention for more than 24 hours.");
        } else if (lower.contains("chest pain") || lower.contains("breath") || lower.contains("shortness of breath")) {
            assessment.setPredictedCondition("Cardiopulmonary Evaluation Required");
            assessment.setSeverityLevel("Emergency");
            assessment.setConfidenceScore("High");
            assessment.setDetectedSymptoms(Arrays.asList("Chest Discomfort", "Shortness of Breath"));
            assessment.setPossibleCauses(Arrays.asList("Cardiovascular strain", "Respiratory distress", "Severe anxiety episode"));
            assessment.setOverview("Symptoms involving chest pain or shortness of breath require priority emergency clinical evaluation.");
            assessment.setRecommendedActions(Arrays.asList(
                "Sit comfortably upright and avoid physical exertion",
                "Seek immediate emergency medical evaluation"
            ));
            assessment.setPrecautions(Arrays.asList("Do not drive yourself to emergency care; call emergency assistance"));
            assessment.setWhenToSeeDoctor("EMERGENCY: Seek immediate hospital emergency services.");
        } else {
            assessment.setPredictedCondition("General Symptom Assessment");
            assessment.setSeverityLevel("Mild");
            assessment.setConfidenceScore("Medium");
            assessment.setDetectedSymptoms(Arrays.asList("General Discomfort"));
            assessment.setPossibleCauses(Arrays.asList("Seasonal physiological variation", "Mild stress or fatigue"));
            assessment.setOverview("General health concern reported. Monitoring and healthy lifestyle habits recommended.");
            assessment.setRecommendedActions(Arrays.asList(
                "Maintain adequate hydration and rest",
                "Keep a daily log of symptoms"
            ));
            assessment.setPrecautions(Arrays.asList("Avoid self-medication without professional consultation"));
            assessment.setWhenToSeeDoctor("Book a consultation with a qualified doctor if symptoms worsen.");
        }

        return assessment;
    }

    /**
     * Sanitizes HealthAssessment strings to ensure NO Markdown symbols (*, #, _, ~, `, etc.) remain.
     */
    public static HealthAssessment sanitizeAssessment(HealthAssessment assessment) {
        if (assessment == null) return null;

        assessment.setPredictedCondition(cleanText(assessment.getPredictedCondition()));
        assessment.setOverview(cleanText(assessment.getOverview()));
        assessment.setWhenToSeeDoctor(cleanText(assessment.getWhenToSeeDoctor()));
        assessment.setRejectionMessage(cleanText(assessment.getRejectionMessage()));

        if (assessment.getDetectedSymptoms() != null) {
            List<String> clean = new ArrayList<>();
            for (String s : assessment.getDetectedSymptoms()) clean.add(cleanText(s));
            assessment.setDetectedSymptoms(clean);
        }
        if (assessment.getPossibleCauses() != null) {
            List<String> clean = new ArrayList<>();
            for (String s : assessment.getPossibleCauses()) clean.add(cleanText(s));
            assessment.setPossibleCauses(clean);
        }
        if (assessment.getRecommendedActions() != null) {
            List<String> clean = new ArrayList<>();
            for (String s : assessment.getRecommendedActions()) clean.add(cleanText(s));
            assessment.setRecommendedActions(clean);
        }
        if (assessment.getPrecautions() != null) {
            List<String> clean = new ArrayList<>();
            for (String s : assessment.getPrecautions()) clean.add(cleanText(s));
            assessment.setPrecautions(clean);
        }

        return assessment;
    }

    /**
     * Strips Markdown formatting symbols (*, #, _, ~, `, bullet signs, etc.) from text string.
     */
    public static String cleanText(String input) {
        if (input == null) return "";

        String text = input;
        // Strip code block fences
        text = text.replaceAll("```[a-zA-Z]*", "");
        // Strip bold/italic asterisks & underscores
        text = text.replaceAll("\\*\\*", "");
        text = text.replaceAll("\\*", "");
        text = text.replaceAll("__", "");
        text = text.replaceAll("_", "");
        // Strip headings
        text = text.replaceAll("#{1,6}\\s*", "");
        // Strip backticks
        text = text.replaceAll("`", "");
        // Strip tildes
        text = text.replaceAll("~", "");
        // Clean leading dashes or list stars
        text = text.replaceAll("(?m)^\\s*[-*+]\\s*", "");

        return text.trim();
    }

    /**
     * Get Groq API key from environment variable or system property.
     */
    private String getGroqApiKey() {
        String envKey = System.getenv("GROQ_API_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey.trim();
        }
        String propKey = System.getProperty("groq.api.key");
        if (propKey != null && !propKey.isBlank()) {
            return propKey.trim();
        }
        return null;
    }
}
