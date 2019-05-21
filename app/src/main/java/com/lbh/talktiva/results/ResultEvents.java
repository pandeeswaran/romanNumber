package com.lbh.talktiva.results;

import com.google.gson.annotations.SerializedName;
import com.lbh.talktiva.model.Event;
import com.lbh.talktiva.model.Links;

import java.util.List;

public class ResultEvents {

    @SerializedName("content")
    private List<Event> content;

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

    public List<Event> getContent() {
        return content;
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
