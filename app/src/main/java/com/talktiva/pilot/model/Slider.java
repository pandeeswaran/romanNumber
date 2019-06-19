package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Slider implements Serializable {

    @SerializedName("text")
    private Integer text;

    @SerializedName("image_url")
    private String imageUrl;

    public Slider(Integer text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public Integer getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
