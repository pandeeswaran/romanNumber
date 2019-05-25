package com.lbh.talktiva.model;

public class GeneralItem extends ListItem {
    private Event event;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }
}
