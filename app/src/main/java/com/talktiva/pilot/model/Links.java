package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Links implements Serializable {

    @SerializedName("next")
    private String next;

    @SerializedName("prev")
    private String prev;

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }
}
