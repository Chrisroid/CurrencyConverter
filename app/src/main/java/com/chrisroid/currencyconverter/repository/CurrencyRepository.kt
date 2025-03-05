package com.chrisroid.currencyconverter.repository

import com.chrisroid.currencyconverter.model.api.CurrencyApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CurrencyRepository @Inject constructor(private val currencyApi : CurrencyApi) {

//    suspend fun getExchangeRate(base: String, apiKey: String): Flow<Result<ExchangeRateResponse>> = flow {
//        try {
//            val response = api.getExchangeRates(apiKey, base)
//            emit(Result.success(response))
//        } catch (e: HttpException) {
//            emit(Result.failure(e))
//        } catch (e: IOException) {
//            emit(Result.failure(e))
//        }
//    }

    suspend fun getCurrencySymbols(apiKey: String): Flow<Result<Map<String, String>>> = flow {
        try {
            val response = currencyApi.getSymbols(apiKey)
            if (response.success) {
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
}
