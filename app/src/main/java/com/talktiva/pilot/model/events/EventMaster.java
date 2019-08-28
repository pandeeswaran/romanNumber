package com.talktiva.pilot.model.events;

import com.talktiva.pilot.model.Event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventMaster implements SectionEvent {

    private Integer day;
    private Boolean bool;
    private Event event;

    public void setHeader(Boolean bool) {
        this.bool = bool;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Nullable
    @Override
    public Integer getDay() {
        return day;
    }

    @Override
    public boolean isHeader() {
        return bool;
    }

    @NotNull
    @Override
    public Event getEvent() {
        return event;
    }
}