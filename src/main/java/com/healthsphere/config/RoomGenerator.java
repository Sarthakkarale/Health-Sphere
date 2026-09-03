package com.healthsphere.config;

import java.util.UUID;

public class RoomGenerator {

    /**
     * Generates a readable and unique Jitsi room ID based on caller and receiver emails/IDs.
     * Special characters are stripped to make it a valid URL segment.
     */
    public static String generateRoomId(String callerEmail, String receiverEmail) {
        String cleanCaller = callerEmail.replaceAll("[^a-zA-Z0-9]", "");
        String cleanReceiver = receiverEmail.replaceAll("[^a-zA-Z0-9]", "");
        
        // Ensure consistent order so either caller/receiver maps to the same name base
        String baseName = cleanCaller.compareTo(cleanReceiver) < 0 
            ? cleanCaller + "_" + cleanReceiver 
            : cleanReceiver + "_" + cleanCaller;
            
        String randomSuffix = UUID.randomUUID().toString().substring(0, 8);
        return "HealthSphere_Call_" + baseName + "_" + randomSuffix;
    }

    /**
     * Generates a pure random UUID based room ID.
     */
    public static String generateRoomIdWithUUID() {
        return "HealthSphere_Call_" + UUID.randomUUID().toString();
    }
}
