package com.chrisroid.currencyconverter.repository

import com.chrisroid.currencyconverter.model.api.CurrencyApi
import com.chrisroid.currencyconverter.model.dao.CurrencyDao
import com.chrisroid.currencyconverter.model.data.CurrencySymbolEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val currencyApi: CurrencyApi,
    private val currencyDao: CurrencyDao
) {

    suspend fun getCurrencySymbols(apiKey: String): Flow<Result<Map<String, String>>> = flow {
        try {
            // Check if symbols exist in Room database
            val localSymbols = currencyDao.getAllSymbols()
            if (localSymbols.isNotEmpty()) {
                emit(Result.success(localSymbols.associate { it.code to it.name }))
                return@flow
            }

            // Fetch from API if database is empty
            val response = currencyApi.getSymbols(apiKey)
            if (response.success) {
                // Convert API response to Room Entity format
                val symbolsToInsert = response.symbols.map { CurrencySymbolEntity(it.key, it.value) }
                currencyDao.insertSymbols(symbolsToInsert)

                emit(Result.success(response.symbols))
            } else {
                emit(Result.failure(Exception("Failed to fetch symbols")))
            }
        } catch (e: HttpException) {
            emit(Result.failure(e))
        } catch (e: IOException) {
            emit(Result.failure(e))
        }
    }

    suspend fun getExchangeRate(base: String, target: String, apiKey: String): Flow<Result<Double>> = flow {
        try {
            val response = currencyApi.getLatestExchangeRate(apiKey, base, target)
            if (response.success) {
                val rate = response.rates[target] ?: 0.0
                emit(Result.success(rate))
            } else {
                emit(Result.failure(Exception("Failed to fetch exchange rate")))
            }
        } catch (e: HttpException) {
            emit(Result.failure(e))
        } catch (e: IOException) {
            emit(Result.failure(e))
        }
    }

}

