package com.healthsphere.dao;

import com.healthsphere.config.Firebase;
import com.healthsphere.model.Call;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CallDao {

    private static Firestore db;
    private static boolean mockMode = false;

    // Simulated Offline/Demo Mode state
    private static final Map<String, Call> mockCalls = new ConcurrentHashMap<>();
    private static final Map<String, List<IncomingCallListener>> mockIncomingListeners = new ConcurrentHashMap<>();
    private static final Map<String, List<CallStatusListener>> mockStatusListeners = new ConcurrentHashMap<>();

    static {
        try {
            db = Firebase.getDB();
            if (db == null) {
                System.out.println("Firebase Firestore is null. Video Call running in Simulated Offline Mode.");
                mockMode = true;
            }
        } catch (Exception e) {
            System.out.println("Failed to initialize Firebase for Video Call: " + e.getMessage() + ". Running in Simulated Offline Mode.");
            mockMode = true;
            db = null;
        }
    }

    public static boolean isMockMode() { return mockMode; }
    public static void setMockMode(boolean mock) { mockMode = mock; }

    public interface IncomingCallListener {
        void onIncomingCall(Call call);
    }

    public interface CallStatusListener {
        void onCallStatusChanged(Call call);
    }

    /**
     * Creates a CALLING document in Firestore (or local map if in mock mode).
     */
    public String startCall(String callerId, String callerName, String receiverId, String receiverName, String roomId) {
        callerId = callerId.trim().toLowerCase();
        receiverId = receiverId.trim().toLowerCase();

        Call call = new Call(callerId, callerName, receiverId, receiverName, roomId, "CALLING");
        
        if (mockMode) {
            String callId = "mock_call_" + UUID.randomUUID().toString();
            call.setCallId(callId);
            mockCalls.put(callId, call);
            triggerMockIncomingCall(receiverId, call);
            return callId;
        } else {
            try {
                DocumentReference docRef = db.collection("calls").document();
                String callId = docRef.getId();
                call.setCallId(callId);
                docRef.set(call).get();
                return callId;
            } catch (Exception e) {
                System.err.println("Firestore startCall failed, falling back to mock: " + e.getMessage());
                String callId = "mock_call_" + UUID.randomUUID().toString();
                call.setCallId(callId);
                mockCalls.put(callId, call);
                triggerMockIncomingCall(receiverId, call);
                return callId;
            }
        }
    }

    /**
     * Gets an existing active call for a room ID or creates a new one if none exists.
     * Ensures both Patient and Doctor attach to the exact same Call document session.
     */
    public Call getOrCreateCallForAppointment(String callerId, String callerName, String receiverId, String receiverName, String roomId) {
        String cleanRoomId = roomId != null ? roomId.trim() : "";
        if (cleanRoomId.isBlank()) {
            String callId = startCall(callerId, callerName, receiverId, receiverName, roomId);
            Call call = new Call(callerId, callerName, receiverId, receiverName, roomId, "CALLING");
            call.setCallId(callId);
            return call;
        }

        if (mockMode) {
            for (Call existing : mockCalls.values()) {
                if (cleanRoomId.equalsIgnoreCase(existing.getRoomId()) &&
                    ("CALLING".equals(existing.getStatus()) || "ACCEPTED".equals(existing.getStatus()))) {
                    if ("CALLING".equals(existing.getStatus())) {
                        existing.setStatus("ACCEPTED");
                        triggerMockCallStatusChanged(existing.getCallId(), existing);
                    }
                    return existing;
                }
            }
            String callId = startCall(callerId, callerName, receiverId, receiverName, roomId);
            Call newCall = mockCalls.get(callId);
            return newCall != null ? newCall : new Call(callerId, callerName, receiverId, receiverName, roomId, "CALLING");
        } else {
            try {
                com.google.api.core.ApiFuture<com.google.cloud.firestore.QuerySnapshot> future = db.collection("calls")
                        .whereEqualTo("roomId", cleanRoomId)
                        .get();
                List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                for (QueryDocumentSnapshot doc : docs) {
                    Call call = doc.toObject(Call.class);
                    if (call != null && ("CALLING".equals(call.getStatus()) || "ACCEPTED".equals(call.getStatus()))) {
                        if ("CALLING".equals(call.getStatus())) {
                            acceptCall(doc.getId());
                            call.setStatus("ACCEPTED");
                        }
                        return call;
                    }
                }
            } catch (Exception e) {
                System.err.println("Firestore getOrCreateCallForAppointment failed: " + e.getMessage());
            }

            String callId = startCall(callerId, callerName, receiverId, receiverName, roomId);
            Call call = new Call(callerId, callerName, receiverId, receiverName, roomId, "CALLING");
            call.setCallId(callId);
            return call;
        }
    }

    /**
     * Updates call status.
     */
    public void updateCallStatus(String callId, String newStatus) {
        if (callId == null) return;
        
        if (mockMode || callId.startsWith("mock_call_")) {
            Call call = mockCalls.get(callId);
            if (call != null) {
                call.setStatus(newStatus);
                triggerMockCallStatusChanged(callId, call);
            }
        } else {
            try {
                db.collection("calls").document(callId).update("status", newStatus).get();
            } catch (Exception e) {
                System.err.println("Firestore updateCallStatus failed: " + e.getMessage());
                Call call = mockCalls.get(callId);
                if (call != null) {
                    call.setStatus(newStatus);
                    triggerMockCallStatusChanged(callId, call);
                }
            }
        }
    }

    public void acceptCall(String callId) { updateCallStatus(callId, "ACCEPTED"); }
    public void declineCall(String callId) { updateCallStatus(callId, "DECLINED"); }
    public void endCall(String callId) { updateCallStatus(callId, "ENDED"); }

    /**
     * Listens for incoming calls targeting a specific receiver ID.
     */
    public ListenerRegistration listenToIncomingCalls(String receiverId, IncomingCallListener listener) {
        final String searchEmail = receiverId.trim().toLowerCase();
        
        if (mockMode) {
            mockIncomingListeners.computeIfAbsent(searchEmail, k -> new CopyOnWriteArrayList<>()).add(listener);
            for (Call call : mockCalls.values()) {
                if (call.getReceiverId().equalsIgnoreCase(searchEmail) && "CALLING".equals(call.getStatus())) {
                    listener.onIncomingCall(call);
                }
            }
            return () -> {
                List<IncomingCallListener> list = mockIncomingListeners.get(searchEmail);
                if (list != null) list.remove(listener);
            };
        } else {
            try {
                return db.collection("calls")
                        .whereEqualTo("receiverId", searchEmail)
                        .whereEqualTo("status", "CALLING")
                        .addSnapshotListener((snapshots, error) -> {
                            if (error != null) {
                                System.err.println("Listen to incoming calls failed: " + error);
                                return;
                            }
                            if (snapshots != null) {
                                for (QueryDocumentSnapshot doc : snapshots.getDocuments()) {
                                    Call call = doc.toObject(Call.class);
                                    listener.onIncomingCall(call);
                                }
                            }
                        });
            } catch (Exception e) {
                System.err.println("Firestore listenToIncomingCalls error, setting up mock: " + e.getMessage());
                mockIncomingListeners.computeIfAbsent(searchEmail, k -> new CopyOnWriteArrayList<>()).add(listener);
                return () -> {
                    List<IncomingCallListener> list = mockIncomingListeners.get(searchEmail);
                    if (list != null) list.remove(listener);
                };
            }
        }
    }

    /**
     * Listens to status updates of a specific call.
     */
    public ListenerRegistration listenToCallStatus(String callId, CallStatusListener listener) {
        if (callId == null) return () -> {};

        if (mockMode || callId.startsWith("mock_call_")) {
            mockStatusListeners.computeIfAbsent(callId, k -> new CopyOnWriteArrayList<>()).add(listener);
            Call current = mockCalls.get(callId);
            if (current != null) listener.onCallStatusChanged(current);
            return () -> {
                List<CallStatusListener> list = mockStatusListeners.get(callId);
                if (list != null) list.remove(listener);
            };
        } else {
            try {
                return db.collection("calls").document(callId)
                        .addSnapshotListener((snapshot, error) -> {
                            if (error != null) {
                                System.err.println("Listen to call status failed: " + error);
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                Call call = snapshot.toObject(Call.class);
                                listener.onCallStatusChanged(call);
                            }
                        });
            } catch (Exception e) {
                System.err.println("Firestore listenToCallStatus error, setting up mock: " + e.getMessage());
                mockStatusListeners.computeIfAbsent(callId, k -> new CopyOnWriteArrayList<>()).add(listener);
                return () -> {
                    List<CallStatusListener> list = mockStatusListeners.get(callId);
                    if (list != null) list.remove(listener);
                };
            }
        }
    }

    private void triggerMockIncomingCall(String receiverId, Call call) {
        List<IncomingCallListener> list = mockIncomingListeners.get(receiverId);
        if (list != null) {
            for (IncomingCallListener l : list) {
                l.onIncomingCall(call);
            }
        }
    }

    private void triggerMockCallStatusChanged(String callId, Call call) {
        List<CallStatusListener> list = mockStatusListeners.get(callId);
        if (list != null) {
            for (CallStatusListener l : list) {
                l.onCallStatusChanged(call);
            }
        }
    }
}
