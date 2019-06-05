package com.talktiva.pilot.results;

import com.google.gson.annotations.SerializedName;
import com.talktiva.pilot.model.Event;
import com.talktiva.pilot.model.Links;

import java.util.List;

public class ResultEvents {

    @SerializedName("content")
    private List<Event> events;

    @SerializedName("totalElements")
    private int totalElements;

    @SerializedName("totalPages")
    private int totalPages;

    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("size")
    private int size;

    @SerializedName("currentPage")
    private int currentPage;

    @SerializedName("links")
    private Links links;

    public List<Event> getEvents() {
        return events;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getSize() {
        return size;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public Links getLinks() {
        return links;
    }
}
