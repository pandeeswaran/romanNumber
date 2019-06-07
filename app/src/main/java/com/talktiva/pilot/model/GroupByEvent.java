package com.talktiva.pilot.model;

import java.util.Date;
import java.util.List;

public class GroupByEvent {

    private String day;
    private List<Event> events;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
