package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Count implements Serializable {

    @SerializedName("eventCount")
    private Integer eventCount;

    public Integer getEventCount() {
        return eventCount;
    }
}
