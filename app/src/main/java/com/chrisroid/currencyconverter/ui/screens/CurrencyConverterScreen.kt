package com.chrisroid.currencyconverter.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.chrisroid.currencyconverter.MainActivity
import com.chrisroid.currencyconverter.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

@Composable
fun CurrencyConverterScreen() {
    var amount by remember { mutableStateOf("1") }
    var selectedBase by remember { mutableStateOf("EUR") }
    var selectedTarget by remember { mutableStateOf("PLN") }
    var conversionRate by remember { mutableStateOf(4.264) } // Dummy value
    var activeTab by remember { mutableStateOf("30 Days") }

    val past30DaysRates = listOf(4.1, 4.15, 4.2, 4.25, 4.26, 4.22, 4.3, 4.28, 4.35, 4.4)

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Menu")
            }
            Text("Sign up", color = Color(0xFF00C896), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text("Currency", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0055FF))
        Text("Calculator.", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0055FF))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))


        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CurrencyDropdown("EUR", R.drawable.germany) { selectedBase = it }
            Text("⇆", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            CurrencyDropdown("PLN", R.drawable.canada) { selectedTarget = it }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Conversion Logic - Placeholder */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Convert")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Mid-market exchange rate at 13:38 UTC", color = Color.Blue, fontSize = 14.sp)
        Text("Exchange Rate: 1 EUR = $conversionRate PLN", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        val context = LocalContext.current
        val activity = context as MainActivity
        ExchangeRateGraph(
            context = context,
            rates = past30DaysRates
        )
    }
}

@Composable
fun CurrencyDropdown(currencyCode: String, flagRes: Int, onSelection: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(0.45f)) {
        Image(painter = painterResource(id = flagRes), contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(currencyCode, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
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
    }, modifier = Modifier.fillMaxWidth().height(200.dp))
}

@Preview(showBackground = true)
@Composable
fun PreviewCurrencyConverterScreen() {
    CurrencyConverterScreen()
}

