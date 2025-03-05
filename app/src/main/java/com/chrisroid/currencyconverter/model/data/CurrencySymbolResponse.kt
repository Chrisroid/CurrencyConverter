package com.chrisroid.currencyconverter.model.data

data class CurrencySymbolResponse(
    val success: Boolean,
    val symbols: Map<String, String>
)
