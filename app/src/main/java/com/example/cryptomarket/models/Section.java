package com.example.cryptomarket.models;

import java.util.List;

public class Section {
    private String title;
    private String icon;
    private List<QuestionAnswer> data;

    public Section(String title, String icon, List<QuestionAnswer> data) {
        this.title = title;
        this.icon = icon;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public List<QuestionAnswer> getData() {
        return data;
    }
}