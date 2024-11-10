package com.example.planmate.user.ui.notifications;

import com.google.firebase.Timestamp;

public class NotificationItem {
    private String eventId;
    private boolean isRead;
    private String message;
    private Timestamp timestamp;  // Change timestamp type to Timestamp
    private String userId;

    // No-argument constructor required for Firestore
    public NotificationItem() {}

    public NotificationItem(String eventId, boolean isRead, String message, Timestamp timestamp, String userId) {
        this.eventId = eventId;
        this.isRead = isRead;
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }
}
