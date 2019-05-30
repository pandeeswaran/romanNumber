package com.lbh.talktiva.model;

import java.util.Date;
import java.util.List;

public class GroupByEvent {

    private Date date;
    private List<Event> events;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
