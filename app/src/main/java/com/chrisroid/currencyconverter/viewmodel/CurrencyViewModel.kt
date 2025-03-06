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
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository // Inject repository for API calls
) : ViewModel() {

    // State flow to hold currency symbols (mapping of currency codes to names)
    private val _currencySymbols = MutableStateFlow<Map<String, String>?>(null)
    val currencySymbols = _currencySymbols.asStateFlow() // Expose as immutable flow

    // State flow to hold exchange rate result
    private val _exchangeRate = MutableStateFlow<Double?>(null)
    val exchangeRate = _exchangeRate.asStateFlow() // Expose as immutable flow

    // State flow to hold converted amount (for UI binding)
    private val _convertedAmount = MutableStateFlow<String>("")
    val convertedAmount = _convertedAmount.asStateFlow() // Expose as immutable flow

    // API key (Consider moving this to a secure config or environment variable)
    private val apiKey = "b1ec7d255c26e3eb047b505fa720cc02"

    init {
        fetchCurrencySymbols() // Fetch currency symbols on ViewModel initialization
    }

    /**
     * Fetches available currency symbols from the API and updates the state flow.
     * Handles potential errors gracefully and logs failures.
     */
    private fun fetchCurrencySymbols() {
        Log.d("CurrencyViewModel", "Fetching currency symbols...")

        viewModelScope.launch {
            repository.getCurrencySymbols(apiKey).collect { result ->
                result.onSuccess { data ->
                    _currencySymbols.value = data // Update state on success
                    Log.d("CurrencyViewModel", "Currency symbols loaded successfully")
                }.onFailure { exception ->
                    // Handle errors and log the failure
                    Log.e("CurrencyViewModel", "Failed to fetch currency symbols", exception)
                }
            }
        }
    }

    /**
     * Fetches the exchange rate between a base currency and target currency.
     * Handles errors and updates the exchange rate state.
     *
     * @param base The base currency (e.g., "USD").
     * @param target The target currency (e.g., "NGN").
     */
    fun fetchExchangeRate(base: String, target: String) {
        Log.d("CurrencyViewModel", "Fetching exchange rate: $base to $target")

        viewModelScope.launch {
            repository.getExchangeRate(base, target, apiKey).collect { result ->
                result.onSuccess { rate ->
                    _exchangeRate.value = rate // Update exchange rate on success
                    Log.d("CurrencyViewModel", "Exchange rate fetched: $rate")
                }.onFailure { exception ->
                    // Handle errors and log failure
                    Log.e("CurrencyViewModel", "Failed to fetch exchange rate", exception)
                }
            }
        }
    }
}
