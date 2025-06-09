package com.example.cryptomarket.models;

public class Exchange {
    private String id;
    private String name;
    private Integer trust_score;
    private int trust_score_rank;
    private String image;
    private Double trade_volume_24h_btc;
    private String description;
    private String country;
    private int year_established;
    private String url;
    private String facebook_url;
    private String twitter_handle;
    private String reddit_url;
    private String telegram_url;
    private int markets;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTrust_score() {
        return trust_score;
    }

    public void setTrust_score(Integer trust_score) {
        this.trust_score = trust_score;
    }

    public int getTrust_score_rank() {
        return trust_score_rank;
    }

    public void setTrust_score_rank(int trust_score_rank) {
        this.trust_score_rank = trust_score_rank;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Double getTrade_volume_24h_btc() {
        return trade_volume_24h_btc;
    }

    public void setTrade_volume_24h_btc(Double trade_volume_24h_btc) {
        this.trade_volume_24h_btc = trade_volume_24h_btc;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getYear_established() {
        return year_established;
    }

    public void setYear_established(int year_established) {
        this.year_established = year_established;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFacebook_url() {
        return facebook_url;
    }

    public void setFacebook_url(String facebook_url) {
        this.facebook_url = facebook_url;
    }

    public String getTwitter_handle() {
        return twitter_handle;
    }

    public void setTwitter_handle(String twitter_handle) {
        this.twitter_handle = twitter_handle;
    }

    public String getReddit_url() {
        return reddit_url;
    }

    public void setReddit_url(String reddit_url) {
        this.reddit_url = reddit_url;
    }

    public String getTelegram_url() {
        return telegram_url;
    }

    public void setTelegram_url(String telegram_url) {
        this.telegram_url = telegram_url;
    }

    public int getMarkets() {
        return markets;
    }

    public void setMarkets(int markets) {
        this.markets = markets;
    }
}