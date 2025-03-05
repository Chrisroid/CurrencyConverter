package com.chrisroid.currencyconverter.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.chrisroid.currencyconverter.MainActivity
import com.chrisroid.currencyconverter.R
import com.chrisroid.currencyconverter.viewmodel.CurrencyViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(viewModel: CurrencyViewModel = hiltViewModel()) {

    var amount by remember { mutableStateOf("1") }
    var convertedAmount by remember { mutableStateOf("4.264820") } // Dummy value for PLN
    var selectedBase by remember { mutableStateOf("EUR") }
    var selectedTarget by remember { mutableStateOf("PLN") }
    var conversionRate by remember { mutableStateOf(4.264820) } // Dummy value
    var activeTab by remember { mutableStateOf("30 Days") }

    val past30DaysRates = listOf(4.1, 4.15, 4.2, 4.25, 4.26, 4.22, 4.3, 4.28, 4.35, 4.4)

    val currencySymbols by viewModel.currencySymbols.collectAsState()


    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
            Text(
                "Sign up",
                color = Color(0xFF00C896),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Currency", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0055FF))
        Text(
            "Calculator.",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0055FF)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // First text field for the base currency (EUR)
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            trailingIcon = {
                Text(
                    text = selectedBase,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF0055FF),
                unfocusedBorderColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Second text field for the target currency (PLN)
        OutlinedTextField(
            value = convertedAmount,
            onValueChange = { convertedAmount = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 18.sp),
            trailingIcon = {
                Text(
                    text = selectedTarget,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF0055FF),
                unfocusedBorderColor = Color.Gray
            ),
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        // Currency selection row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CurrencyDropdown(
                selectedCurrency = selectedBase,
                currencySymbols = currencySymbols,
                onCurrencySelected = { selectedBase = it }
            )
            Text("→", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            CurrencyDropdown(
                selectedCurrency = selectedTarget,
                currencySymbols = currencySymbols,
                onCurrencySelected = { selectedTarget = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Convert button
        Button(
            onClick = { /* Conversion Logic - Placeholder */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C896))
        ) {
            Text("Convert", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exchange rate information
        Text("Mid-market exchange rate at 13:38 UTC", color = Color.Gray, fontSize = 14.sp)
        Text(
            "Exchange Rate: 1 EUR = $conversionRate PLN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Exchange rate graph
        val context = LocalContext.current
        ExchangeRateGraph(
            context = context,
            rates = past30DaysRates
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs for 30 Days and 90 Days
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TabButton("30 Days", activeTab == "30 Days") { activeTab = it }
            TabButton("90 Days", activeTab == "90 Days") { activeTab = it }
        }
    }
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    currencySymbols: Map<String, String>?,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth(0.45f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedCurrency, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.45f)
        ) {
            currencySymbols?.forEach { (code, name) ->
                DropdownMenuItem(
                    text = { Text("$code") },
                    onClick = {
                        onCurrencySelected(code)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ExchangeRateGraph(context: Context, rates: List<Double>) {
    AndroidView(factory = {
        LineChart(it).apply {
            val entries = rates.mapIndexed { index, rate ->
                Entry(index.toFloat(), rate.toFloat())
            }
            val dataSet = LineDataSet(entries, "Exchange Rate")
            dataSet.color = android.graphics.Color.BLUE
            dataSet.valueTextColor = android.graphics.Color.BLACK
            val lineData = LineData(dataSet)
            this.data = lineData
            this.invalidate()
        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(200.dp))
}

@Composable
fun TabButton(text: String, isActive: Boolean, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(text) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) Color(0xFF00C896) else Color.LightGray,
            contentColor = if (isActive) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrencyConverterScreen() {
    CurrencyConverterScreen()
}
