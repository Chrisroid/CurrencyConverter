package com.chrisroid.currencyconverter.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrisroid.currencyconverter.model.data.CurrencySymbolEntity

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency_symbols")
    suspend fun getAllSymbols(): List<CurrencySymbolEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbols(symbols: List<CurrencySymbolEntity>)

    @Query("SELECT COUNT(*) FROM currency_symbols")
    suspend fun getSymbolsCount(): Int
}
