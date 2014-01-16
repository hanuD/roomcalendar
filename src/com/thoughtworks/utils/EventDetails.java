package com.thoughtworks.utils;

import java.io.Serializable;
import java.util.Date;

public class EventDetails implements Serializable, Comparable<EventDetails> {
    private String name;
    private String eventId;
    private String place;
    private Date eventstart;
    private Date eventend;
    private String author;


    public EventDetails(String name, String eventId, String place, Date eventstart, Date eventend, String author) {
        this.name = name;
        this.eventId = eventId;
        this.place = place;
        this.eventstart = eventstart;
        this.eventend = eventend;
        this.author = author;
    }

    public EventDetails() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setEventstart(Date eventstart) {
        this.eventstart = eventstart;
    }

    public Date getEventStart() {
        return eventstart;
    }

    public void setEventend(Date eventend) {
        this.eventend = eventend;
    }

    public Date getEventEnd() {
        return eventend;
    }

    @Override
    public String toString() {
        return "MyEventDTO{" +
                "name='" + name + '\'' +
                ", eventId='" + eventId + '\'' +
                ", place='" + place + '\'' +
                ", eventstart=" + eventstart +
                ", eventend=" + eventend +
                '}';
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public int compareTo(EventDetails another) {
//        int count = 0;
//        Pattern pattern = Pattern.compile("Room");
////        Matcher thisMatcher = pattern.matcher(this.getPlace());
//        Matcher anotherMatcher = pattern.matcher(another.getPlace());
//        while (anotherMatcher.find()) {
//            count++;
//        }
//
//        if (count > 1) {
//            return 1;
//        } else {
            if (this.getEventStart() != null && this.getEventEnd() != null) {
                if (this.getEventStart().after(another.getEventStart()) && this.getEventEnd().after(another.getEventEnd())) {
                    return 1;
                } else if (this.getEventStart().before(another.getEventStart()) && this.getEventEnd().before(another.getEventEnd())) {
                    return -1;
                } else {
                    return 0;
                }
            }
            return 0;
        }



}