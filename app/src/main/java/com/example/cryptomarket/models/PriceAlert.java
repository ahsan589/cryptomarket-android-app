package com.example.cryptomarket.models;

public class PriceAlert {

    private String coinName;
    private String alertType;
    private float targetPrice;


    public PriceAlert(String coinName, String alertType, float targetPrice) {
        this.coinName = coinName;
        this.alertType = alertType;
        this.targetPrice = targetPrice;

    }

    public String getCoinName() {
        return coinName;
    }

    public String getAlertType() {
        return alertType;
    }

    public float getTargetPrice() {
        return targetPrice;
    }


}
