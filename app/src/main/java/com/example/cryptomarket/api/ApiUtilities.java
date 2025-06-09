package com.example.cryptomarket.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiUtilities {
    private static final String COINMARKETCAP_BASE_URL = "https://api.coinmarketcap.com/";
    private static final String COINGECKO_BASE_URL = "https://api.coingecko.com/api/";

    private static Retrofit coinMarketCapRetrofit = null;
    private static Retrofit coinGeckoRetrofit = null;

    // Instance for CoinMarketCap API
    public static Retrofit getInstance() {
        if (coinMarketCapRetrofit == null) {
            coinMarketCapRetrofit = new Retrofit.Builder()
                    .baseUrl(COINMARKETCAP_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return coinMarketCapRetrofit;
    }

    // Instance for CoinGecko API
    public static Retrofit getClient() {
        if (coinGeckoRetrofit == null) {
            coinGeckoRetrofit = new Retrofit.Builder()
                    .baseUrl(COINGECKO_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return coinGeckoRetrofit;
    }

    public static Apiinterface getCoinMarketCapApiInterface() {
        return getInstance().create(Apiinterface.class);
    }

    public static Apiinterface getCoinGeckoApiInterface() {
        return getClient().create(Apiinterface.class);
    }
}
