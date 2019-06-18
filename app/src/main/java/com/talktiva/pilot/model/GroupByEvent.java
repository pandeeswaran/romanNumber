package com.talktiva.pilot.model;

import java.util.List;

public class GroupByEvent {

    private Integer day;
    private List<Event> events;

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
