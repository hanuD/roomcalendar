package com.thoughtworks.utils;

import java.util.Calendar;

public class BookingDetails {
    String eventName;
    String organizer;
    Calendar startTime;
    Calendar endTime;

    public BookingDetails(String eventName, String organizer, Calendar startTime, Calendar endTime) {
        this.eventName = eventName;
        this.organizer = organizer;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getOrganizer() {
        return organizer;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

}
