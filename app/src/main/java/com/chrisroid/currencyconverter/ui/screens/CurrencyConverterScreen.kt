package com.chrisroid.currencyconverter.ui.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.murgupluoglu.flagkit.FlagKit

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
                onCurrencySelected = { selectedBase = it }
            )
            Text("→", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            CurrencyDropdown(
                selectedCurrency = selectedTarget,
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
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxWidth(0.45f)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val countryCode = currencyToCountryMap[selectedCurrency] ?: "xx" // Default if not found
            val flagResId = when (selectedCurrency) {
                "ANG" -> R.drawable.angola  // Custom Angola flag
                "BTC" -> R.drawable.btc // Custom Bitcoin flag
                else -> FlagKit.getResId(context, countryCode)
            }

            if (flagResId != 0) {
                Image(
                    painter = painterResource(id = flagResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(selectedCurrency, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.45f)
        ) {
            currencyToCountryMap.keys.forEach { currencyCode ->
                DropdownMenuItem(
                    onClick = {
                        onCurrencySelected(currencyCode)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val flagResId = when (currencyCode) {
                                "ANG" -> R.drawable.angola  // Angola special case
                                "BTC" -> R.drawable.btc // Bitcoin special case
                                else -> FlagKit.getResId(context, currencyToCountryMap[currencyCode] ?: "xx")
                            }

                            if (flagResId != 0) {
                                Image(
                                    painter = painterResource(id = flagResId),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(currencyCode, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
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

val currencyToCountryMap = mapOf(
    "AED" to "ae", "AFN" to "af", "ALL" to "al", "AMD" to "am", "ANG" to "an",
    "AOA" to "ao", "ARS" to "ar", "AUD" to "au", "AWG" to "aw", "AZN" to "az",
    "BAM" to "ba", "BBD" to "bb", "BDT" to "bd", "BGN" to "bg", "BHD" to "bh",
    "BIF" to "bi", "BMD" to "bm", "BND" to "bn", "BOB" to "bo", "BRL" to "br",
    "BSD" to "bs", "BTC" to "xx", "BTN" to "bt", "BWP" to "bw", "BYN" to "by",
    "CAD" to "ca", "CDF" to "cd", "CHF" to "ch", "CNY" to "cn", "COP" to "co",
    "CRC" to "cr", "CUP" to "cu", "CVE" to "cv", "CZK" to "cz", "DJF" to "dj",
    "DKK" to "dk", "DOP" to "do", "DZD" to "dz", "EGP" to "eg", "ERN" to "er",
    "ETB" to "et", "EUR" to "eu", "FJD" to "fj", "FKP" to "fk", "GBP" to "gb",
    "GEL" to "ge", "GHS" to "gh", "GIP" to "gi", "GMD" to "gm", "GNF" to "gn",
    "GTQ" to "gt", "GYD" to "gy", "HKD" to "hk", "HNL" to "hn", "HRK" to "hr",
    "HTG" to "ht", "HUF" to "hu", "IDR" to "id", "ILS" to "il", "INR" to "in",
    "IQD" to "iq", "IRR" to "ir", "ISK" to "is", "JMD" to "jm", "JOD" to "jo",
    "JPY" to "jp", "KES" to "ke", "KGS" to "kg", "KHR" to "kh", "KMF" to "km",
    "KPW" to "kp", "KRW" to "kr", "KWD" to "kw", "KYD" to "ky", "KZT" to "kz",
    "LAK" to "la", "LBP" to "lb", "LKR" to "lk", "LRD" to "lr", "LSL" to "ls",
    "LYD" to "ly", "MAD" to "ma", "MDL" to "md", "MGA" to "mg", "MKD" to "mk",
    "MMK" to "mm", "MNT" to "mn", "MOP" to "mo", "MRO" to "mr", "MUR" to "mu",
    "MVR" to "mv", "MWK" to "mw", "MXN" to "mx", "MYR" to "my", "MZN" to "mz",
    "NAD" to "na", "NGN" to "ng", "NIO" to "ni", "NOK" to "no", "NPR" to "np",
    "NZD" to "nz", "OMR" to "om", "PAB" to "pa", "PEN" to "pe", "PGK" to "pg",
    "PHP" to "ph", "PKR" to "pk", "PLN" to "pl", "PYG" to "py", "QAR" to "qa",
    "RON" to "ro", "RSD" to "rs", "RUB" to "ru", "RWF" to "rw", "SAR" to "sa",
    "SBD" to "sb", "SCR" to "sc", "SDG" to "sd", "SEK" to "se", "SGD" to "sg",
    "SHP" to "sh", "SLL" to "sl", "SOS" to "so", "SRD" to "sr", "STD" to "st",
    "SVC" to "sv", "SYP" to "sy", "SZL" to "sz", "THB" to "th", "TJS" to "tj",
    "TMT" to "tm", "TND" to "tn", "TOP" to "to", "TRY" to "tr", "TTD" to "tt",
    "TWD" to "tw", "TZS" to "tz", "UAH" to "ua", "UGX" to "ug", "USD" to "us",
    "UYU" to "uy", "UZS" to "uz", "VEF" to "ve", "VND" to "vn", "VUV" to "vu",
    "WST" to "ws", "XAF" to "cm", "XCD" to "ag", "XOF" to "sn", "XPF" to "pf",
    "YER" to "ye", "ZAR" to "za", "ZMK" to "zm", "ZMW" to "zm", "ZWL" to "zw"
)

