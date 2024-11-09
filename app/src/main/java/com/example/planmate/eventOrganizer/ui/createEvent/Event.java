package com.example.planmate.eventOrganizer.ui.createEvent;

public class Event {
    private String eventName;
    private String eventDate;
    private String eventLocation;
    private String eventDescription;
    private String organizerId;

    // Empty constructor for Firestore deserialization
    public Event() {}

    public Event(String eventName, String eventDate, String eventLocation, String eventDescription, String organizerId) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.organizerId = organizerId;
    }

    // Getters and setters
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventDate() { return eventDate; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }

    public String getEventLocation() { return eventLocation; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
}
