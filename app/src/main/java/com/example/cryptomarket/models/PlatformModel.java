package com.example.cryptomarket.models;

public class PlatformModel {
    private String name;
    private String url;
    private int iconRes;


    // Constructor
    public PlatformModel(String name, String url, int iconRes) {
        this.name = name;
        this.url = url;
        this.iconRes = iconRes;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public int getIconRes() {
        return iconRes;
    }


}