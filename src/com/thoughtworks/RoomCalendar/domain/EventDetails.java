package com.thoughtworks.RoomCalendar.domain;

import java.io.Serializable;

public class EventDetails implements Serializable{

    long startTime;
    long endTime;
    String organizer;
    String eventName;
    int attendees;
    long eventId;
    String location;

    public EventDetails() {

    }


    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getOrganizer() {
        return organizer;
    }

    public String getEventName() {
        return eventName;
    }

    public int getAttendees() {
        return attendees;
    }

    public long getEventId() {
        return eventId;
    }

    public String getLocation() {
        return location;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setAttendees(int attendees) {
        this.attendees = attendees;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}