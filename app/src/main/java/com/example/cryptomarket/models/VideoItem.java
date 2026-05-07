package com.example.cryptomarket.models;

import com.google.gson.annotations.SerializedName;

public class VideoItem {
    @SerializedName("title")
    private String title;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("duration")
    private String duration;
    
    @SerializedName("views")
    private int views;
    
    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;
    
    @SerializedName("videoUrl")
    private String videoUrl;
    
    // Progress is not in JSON, but kept for compatibility
    private int progress;

    // Constructors
    public VideoItem() {}

    public VideoItem(String title, String description, String duration, int views, String thumbnailUrl, String videoUrl) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.views = views;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.progress = 0;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
}
