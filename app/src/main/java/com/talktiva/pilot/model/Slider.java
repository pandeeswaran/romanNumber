package com.talktiva.pilot.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Slider implements Serializable {

    @SerializedName("text")
    private String text;

    @SerializedName("image_url")
    private String imageUrl;

    public Slider(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
