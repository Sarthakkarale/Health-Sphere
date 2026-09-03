package com.healthsphere.model;

import com.google.cloud.firestore.annotation.DocumentId;
import java.util.Date;

public class Call {

    @DocumentId
    private String callId;

    private String callerId;      // Email/ID of the caller
    private String callerName;    // Display name of caller
    private String receiverId;    // Email/ID of the receiver
    private String receiverName;  // Display name of receiver
    private String roomId;        // Jitsi Meet Room ID
    private String status;        // CALLING, ACCEPTED, DECLINED, ENDED
    private Date createdAt;       // Timestamp of call start

    public Call() {
    }

    public Call(String callerId, String callerName, String receiverId, String receiverName, String roomId, String status) {
        this.callerId = callerId;
        this.callerName = callerName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.roomId = roomId;
        this.status = status;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public String getCallId() { return callId; }
    public void setCallId(String callId) { this.callId = callId; }
    public String getCallerId() { return callerId; }
    public void setCallerId(String callerId) { this.callerId = callerId; }
    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Call{" +
                "callId='" + callId + '\'' +
                ", callerId='" + callerId + '\'' +
                ", callerName='" + callerName + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", receiverName='" + receiverName + '\'' +
                ", roomId='" + roomId + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
