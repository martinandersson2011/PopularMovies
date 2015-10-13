package com.martinandersson.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class VideosResponse implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Video> videoList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }
}
