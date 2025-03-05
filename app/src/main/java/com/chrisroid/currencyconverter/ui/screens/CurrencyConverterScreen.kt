package com.chrisroid.currencyconverter.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chrisroid.currencyconverter.viewmodel.CurrencyViewModel

@Composable
fun CurrencyConverterScreen(viewModel : CurrencyViewModel) {
    var amount by remember { mutableStateOf("") }
    var selectedBase by remember { mutableStateOf("EUR") }
    var selectedTarget by remember { mutableStateOf("USD") }
    val exchangeRates by viewModel.exchangeRate.collectAsState()
    val currencySymbols by viewModel.currencySymbols.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCurrencySymbols("812b144374f18f73fd5fa73bd008dc1b")
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Currency Calculator", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            item {
                currencySymbols?.let { symbols ->
                    Row {
                        DropdownMenuBox(
                            label = "Base Currency",
                            selectedCurrency = selectedBase,
                            currencies = symbols.keys.toList()
                        ) { selectedBase = it }

                        Spacer(modifier = Modifier.width(8.dp))

                        DropdownMenuBox(
                            label = "Target Currency",
                            selectedCurrency = selectedTarget,
                            currencies = symbols.keys.toList()
                        ) { selectedTarget = it }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
//                viewModel.fetchExchangeRate(selectedBase, "812b144374f18f73fd5fa73bd008dc1b")
                      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        Spacer(modifier = Modifier.height(16.dp))

        exchangeRates?.let {
            val rate = it[selectedTarget] ?: 0.0
            val convertedAmount = amount.toDoubleOrNull()?.times(rate) ?: 0.0
            Text("Conversion Rate: $rate")
            Text("Converted Amount: $convertedAmount")
        }
    }
}

@Composable
fun DropdownMenuBox(label: String, selectedCurrency: String, currencies: List<String>, onSelection: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label)
        Button(onClick = { expanded = true }) { Text(selectedCurrency) }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    onClick = {
                        onSelection(currency)
                        expanded = false
                    },
                    text = { Text(currency) }
                )
            }
        }
    }
}
