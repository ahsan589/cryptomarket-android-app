package com.example.cryptomarket.network;

import com.example.cryptomarket.models.NewsResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    // Fetch general news with pagination
    @GET("data/v2/news/")
    Call<NewsResponse> getNews();

    // Fetch news for a specific coin with pagination
    @GET("data/v2/news/")
    Call<NewsResponse> getNewsByCoin(
            @Query("symbol") String symbol,
            @Query("lTs") Long lastTimestamp
    );
}