package com.chrisroid.currencyconverter.model.api

import com.chrisroid.currencyconverter.model.data.CurrencySymbolResponse
import com.chrisroid.currencyconverter.model.data.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("symbols")
    suspend fun getSymbols(
        @Query("access_key") apiKey: String
    ): CurrencySymbolResponse

    @GET("latest")
    suspend fun getLatestExchangeRate(
        @Query("access_key") apiKey: String,
        @Query("base") base: String,
        @Query("symbols") symbol: String // Only one symbol at a time
    ): ExchangeRateResponse

}