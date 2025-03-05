package com.chrisroid.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrisroid.currencyconverter.repository.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repository: CurrencyRepository) : ViewModel(){

    private val _currencySymbols = MutableStateFlow<Map<String, String>?>(null)
    val currencySymbols = _currencySymbols.asStateFlow()

    private val _exchangeRate = MutableStateFlow<Double?>(null)
    val exchangeRate = _exchangeRate.asStateFlow()

    private val _convertedAmount = MutableStateFlow<String>("")
    val convertedAmount = _convertedAmount.asStateFlow()


    init {
        fetchCurrencySymbols()
    }
//    fun fetchExchangeRate(base: String, apiKey: String) {
//        viewModelScope.launch {
//            repository.getExchangeRate(base, apiKey).collect { result ->
//                result.onSuccess { data ->
//                    _exchangeRate.value = data.rates
//                }
//            }
//        }
//    }

    fun fetchCurrencySymbols() {
        Log.d("FETCH", "Checking if database is empty before fetching")
        val apiKey = "b1ec7d255c26e3eb047b505fa720cc02"
        viewModelScope.launch {
            repository.getCurrencySymbols(apiKey).collect { result ->
                result.onSuccess { data ->
                    _currencySymbols.value = data
                }
            }
        }
    }

    fun fetchExchangeRate(base: String, target: String) {
        viewModelScope.launch {
            val apiKey = "b1ec7d255c26e3eb047b505fa720cc02"
            repository.getExchangeRate(base, target, apiKey).collect { result ->
                result.onSuccess { rate ->
                    _exchangeRate.value = rate
                }
            }
        }
    }


}
