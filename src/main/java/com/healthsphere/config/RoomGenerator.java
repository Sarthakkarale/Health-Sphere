package com.healthsphere.config;

import java.util.UUID;

public class RoomGenerator {

    /**
     * Generates a deterministic Jitsi room ID derived from an appointment ID.
     * Guaranteed to produce the exact same room ID for both Doctor and Patient joining the appointment.
     */
    public static String generateRoomIdForAppointment(String appointmentId) {
        if (appointmentId == null || appointmentId.isBlank()) {
            return "HealthSphere_Consultation_General";
        }
        String cleanAptId = appointmentId.replaceAll("[^a-zA-Z0-9]", "");
        return "HealthSphere_Consultation_" + cleanAptId;
    }

    /**
     * Generates a readable and unique Jitsi room ID based on caller and receiver emails/IDs.
     * Special characters are stripped to make it a valid URL segment.
     */
    public static String generateRoomId(String callerEmail, String receiverEmail) {
        String cleanCaller = callerEmail != null ? callerEmail.replaceAll("[^a-zA-Z0-9]", "") : "user1";
        String cleanReceiver = receiverEmail != null ? receiverEmail.replaceAll("[^a-zA-Z0-9]", "") : "user2";
        
        // Ensure consistent order so either caller/receiver maps to the same room name
        String baseName = cleanCaller.compareTo(cleanReceiver) < 0 
            ? cleanCaller + "_" + cleanReceiver 
            : cleanReceiver + "_" + cleanCaller;
            
        return "HealthSphere_Call_" + baseName;
    }

    /**
     * Generates a pure random UUID based room ID.
     */
    public static String generateRoomIdWithUUID() {
        return "HealthSphere_Call_" + UUID.randomUUID().toString();
    }
}
