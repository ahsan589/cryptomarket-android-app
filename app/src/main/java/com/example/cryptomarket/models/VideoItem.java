package com.example.cryptomarket.models;

public class VideoItem {
    private String title;
    private String description;
    private String duration;
    private int progress;
    private String thumbnailUrl;
    private String videoUrl;

    // Constructor with all fields
    public VideoItem(String title, String description, String duration, int progress, String thumbnailUrl, String videoUrl) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.progress = progress;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
    }

    // Constructor with only title and videoUrl (for backward compatibility)
    public VideoItem(String title, String videoUrl) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.description = ""; // Default empty description
        this.duration = "0:00"; // Default duration
        this.progress = 0; // Default progress
        this.thumbnailUrl = ""; // Default empty thumbnail URL
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDuration() {
        return duration;
    }

    public int getProgress() {
        return progress;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    // Setters (optional, if you need to modify fields after creation)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}