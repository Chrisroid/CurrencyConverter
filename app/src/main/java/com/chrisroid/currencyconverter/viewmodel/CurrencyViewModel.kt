package com.chrisroid.currencyconverter.viewmodel

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


    private val _exchangeRate = MutableStateFlow<Map<String, Double>?>(null)
    val exchangeRate = _exchangeRate.asStateFlow()

    private val _currencySymbols = MutableStateFlow<Map<String, String>?>(null)
    val currencySymbols = _currencySymbols.asStateFlow()

//    fun fetchExchangeRate(base: String, apiKey: String) {
//        viewModelScope.launch {
//            repository.getExchangeRate(base, apiKey).collect { result ->
//                result.onSuccess { data ->
//                    _exchangeRate.value = data.rates
//                }
//            }
//        }
//    }

    fun fetchCurrencySymbols(apiKey: String) {
        viewModelScope.launch {
            repository.getCurrencySymbols(apiKey).collect { result ->
                result.onSuccess { data ->
                    _currencySymbols.value = data
                }
            }
        }
    }
}
