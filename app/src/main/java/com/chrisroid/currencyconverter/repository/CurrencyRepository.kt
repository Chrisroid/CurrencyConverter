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
    private val currencyApi: CurrencyApi, // API service to get currency data
    private val currencyDao: CurrencyDao // DAO to interact with local database (Room)
) {

    /**
     * Fetches currency symbols.
     * First attempts to retrieve data from the local database (Room).
     * If the database is empty, fetches data from the API and saves it in the database.
     *
     * @param apiKey The API key to authenticate the request.
     * @return A Flow emitting a Result containing the currency symbols or an error.
     */
    suspend fun getCurrencySymbols(apiKey: String): Flow<Result<Map<String, String>>> = flow {
        try {
            // Check if symbols exist in Room database
            val localSymbols = currencyDao.getAllSymbols()
            if (localSymbols.isNotEmpty()) {
                // Emit local data if available
                emit(Result.success(localSymbols.associate { it.code to it.name }))
                return@flow
            }

            // Fetch symbols from the API if database is empty
            val response = currencyApi.getSymbols(apiKey)

            // Check if the API request was successful
            if (response.success) {
                // Convert API response into Room Entity format and insert into database
                val symbolsToInsert = response.symbols.map { CurrencySymbolEntity(it.key, it.value) }
                currencyDao.insertSymbols(symbolsToInsert)

                // Emit the symbols fetched from API
                emit(Result.success(response.symbols))
            } else {
                // Emit failure if the API request was unsuccessful
                emit(Result.failure(Exception("Failed to fetch symbols")))
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions, such as 404 or 500 errors
            emit(Result.failure(e))
        } catch (e: IOException) {
            // Handle network-related exceptions, such as no internet connection
            emit(Result.failure(e))
        }
    }

    /**
     * Fetches the exchange rate between the base and target currencies.
     * Fetches data from the API and returns the result as a Flow.
     *
     * @param base The base currency code (e.g., "USD").
     * @param target The target currency code (e.g., "EUR").
     * @param apiKey The API key to authenticate the request.
     * @return A Flow emitting a Result containing the exchange rate or an error.
     */
    suspend fun getExchangeRate(base: String, target: String, apiKey: String): Flow<Result<Double>> = flow {
        try {
            // Fetch the exchange rate from the API
            val response = currencyApi.getLatestExchangeRate(apiKey, base, target)

            // Check if the API response was successful
            if (response.success) {
                // Extract the exchange rate for the target currency
                val rate = response.rates[target] ?: 0.0 // Default to 0.0 if no rate found
                emit(Result.success(rate)) // Emit the rate as a successful result
            } else {
                // Emit failure if the API request was unsuccessful
                emit(Result.failure(Exception("Failed to fetch exchange rate")))
            }
        } catch (e: HttpException) {
            // Handle HTTP exceptions, such as 404 or 500 errors
            emit(Result.failure(e))
        } catch (e: IOException) {
            // Handle network-related exceptions, such as no internet connection
            emit(Result.failure(e))
        }
    }
}
