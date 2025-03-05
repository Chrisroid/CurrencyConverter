package com.chrisroid.currencyconverter.model.api

import com.chrisroid.currencyconverter.model.data.CurrencySymbolResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("symbols")
    suspend fun getSymbols(
        @Query("access_key") apiKey: String
    ): CurrencySymbolResponse
}