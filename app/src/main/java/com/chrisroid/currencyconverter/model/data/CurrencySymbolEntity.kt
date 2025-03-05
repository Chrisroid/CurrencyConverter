package com.chrisroid.currencyconverter.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_symbols")
data class CurrencySymbolEntity(
    @PrimaryKey val code: String,
    val name: String
)
