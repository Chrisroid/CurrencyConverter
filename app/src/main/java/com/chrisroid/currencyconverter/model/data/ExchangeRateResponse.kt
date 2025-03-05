package com.chrisroid.currencyconverter.model.data

data class ExchangeRateResponse(
    val success: Boolean,
    val timestamp: Long,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
