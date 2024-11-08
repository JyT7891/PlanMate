package com.example.planmate.user.ui.event;

import java.util.List;

public class Event {
    private String eventId;        // Unique identifier for the event
    private String eventName;      // Name of the event
    private String eventDate;      // Date of the event
    private String eventTime;      // Time of the event
    private String eventLocation;  // Location of the event
    private String eventDescription; // Description of the event
    private List<String> attendees; // List of user IDs or names of attendees

    // Constructor
    public Event(String eventId, String eventName, String eventDate, String eventTime, String eventLocation, String eventDescription, List<String> attendees) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventDescription = eventDescription;
        this.attendees = attendees;
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }
}
