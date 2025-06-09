package com.example.cryptomarket.api

import com.example.cryptomarket.models.Exchange
import com.example.cryptomarket.models.MarketModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Apiinterface {
    @get:GET("data-api/v3/cryptocurrency/listing?start=1&limit=1000")
    val marketData: Call<MarketModel?>?

    @GET("v3/exchanges")
    fun getExchangesData(): Call<List<Exchange?>?>?

    // Fetch details of a specific exchange by its ID
    @GET("v3/exchanges/{exchangeId}")
    fun getExchangeDetails(@Path("exchangeId") exchangeId: String): Call<Exchange>
}
