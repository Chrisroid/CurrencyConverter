package com.chrisroid.currencyconverter.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.chrisroid.currencyconverter.R
import com.chrisroid.currencyconverter.viewmodel.CurrencyViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.murgupluoglu.flagkit.FlagKit
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyConverterScreen(viewModel: CurrencyViewModel = hiltViewModel()) {

    var amount by remember { mutableStateOf("1") }
    var selectedBase by remember { mutableStateOf("EUR") }
    var selectedTarget by remember { mutableStateOf("NGN") }
    val exchangeRate by viewModel.exchangeRate.collectAsState()
    val convertedAmount by remember(amount, exchangeRate) {
        mutableStateOf(
            String.format("%.2f", (amount.toDoubleOrNull() ?: 0.0) * (exchangeRate ?: 1.0))
        )
    }
    var activeTab by remember { mutableStateOf("30 Days") }

    val past30DaysRates = listOf(4.1, 4.15, 4.2, 4.25, 4.26, 4.22, 4.3, 4.28, 4.35, 4.4)
    val past30DaysDates = getLast30DaysLabels()

    val currencySymbols by viewModel.currencySymbols.collectAsState()



    LaunchedEffect(selectedTarget, selectedBase) {
        viewModel.fetchExchangeRate(selectedBase, selectedTarget)
    }

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

        Text("Currency", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0055FF))
        Text(
            "Calculator.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0055FF)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // First text field for the base currency (EUR)
        OutlinedTextField(
            value = amount,
            onValueChange = { newValue ->
                val numericValue = newValue.toDoubleOrNull() ?: 1.0
                amount = if (numericValue < 1) "1" else numericValue.toInt().toString()
            },
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
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            )
        )



        Spacer(modifier = Modifier.height(8.dp))

        // Second text field for the target currency (PLN)
        OutlinedTextField(
            value = convertedAmount,
            onValueChange = { }, // Read-only field
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CurrencyDropdown(
                selectedCurrency = selectedBase,
                onCurrencySelected = { selectedBase = it },
                modifier = Modifier.weight(1f),
                isBaseCurrency = true
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("←", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("→", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.width(16.dp))

            CurrencyDropdown(
                selectedCurrency = selectedTarget,
                onCurrencySelected = { selectedTarget = it },
                modifier = Modifier.weight(1f),
                isBaseCurrency = false
            )
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Convert button
        Button(
            onClick = {
                viewModel.fetchExchangeRate(selectedBase, selectedTarget)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C896))
        ) {
            Text("Convert", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Exchange rate information
        Text(
            text = "Mid-market exchange rate at 13:38 UTC",
            color = Color(0xFF0055FF), // Blue color
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth() // Ensures it takes full width
                .clickable { /* Handle click if needed */ },
            textAlign = TextAlign.Center, // Centers text horizontally
            style = TextStyle(textDecoration = TextDecoration.Underline) // Underline effect
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Exchange Rate: $amount $selectedBase = $convertedAmount $selectedTarget",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0066FF)) // Blue background
                .padding(16.dp) // Padding inside the box
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tabs for 30 Days and 90 Days
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabButton("30 Days", activeTab == "30 Days") { activeTab = it }
                    TabButton("90 Days", activeTab == "90 Days") { activeTab = it }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Exchange Rate Graph
                ExchangeRateGraph(rates = past30DaysRates, dates = past30DaysDates)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }



    }
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isBaseCurrency: Boolean // Determines if this is the base or target dropdown
) {
    var expanded by remember { mutableStateOf(false) } // State for dropdown visibility
    val context = LocalContext.current

    // Precompute flag images to prevent UI lag
    val flagCache = remember {
        currencyToCountryMap.mapValues { (code, countryCode) ->
            when (code) {
                "ANG" -> R.drawable.angola  // Custom Angola flag
                "BTC" -> R.drawable.btc // Custom Bitcoin flag
                else -> FlagKit.getResId(context, countryCode) // Get country flag resource ID
            }
        }
    }

    // Get the list of currencies available for selection
    val availableCurrencies = if (isBaseCurrency) listOf("EUR") else currencyToCountryMap.keys.filter { it != "EUR" }

    Box(
        modifier = modifier
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .fillMaxWidth()
    ) {
        // Dropdown button layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true } // Open dropdown when clicked
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val flagResId = flagCache[selectedCurrency] ?: R.drawable.btc
            Image(
                painter = painterResource(id = flagResId),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Display selected currency
            Text(
                text = selectedCurrency,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f) // Ensures balanced dropdown alignment
            )

            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                modifier = Modifier.size(24.dp) // Dropdown arrow
            )
        }

        // Dropdown menu for selecting currency
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Close dropdown on selection
            modifier = Modifier.fillMaxWidth(0.45f) // Adjust dropdown width
        ) {
            availableCurrencies.forEach { currencyCode ->
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onCurrencySelected(currencyCode)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val flagResId = flagCache[currencyCode] ?: R.drawable.btc
                            Image(
                                painter = painterResource(id = flagResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                            )
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
fun ExchangeRateGraph(rates: List<Double>, dates: List<String>) {
    AndroidView(factory = { context ->
        LineChart(context).apply {
            // Prepare data points for the line chart
            val entries = rates.mapIndexed { index, rate ->
                Entry(index.toFloat(), rate.toFloat())
            }

            // Configure dataset for the graph
            val dataSet = LineDataSet(entries, "Exchange Rate").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER // Smooth curve line
                color = android.graphics.Color.parseColor("#3388FF") // Line color
                valueTextColor = android.graphics.Color.WHITE // Hide value labels
                setDrawCircles(false) // Remove point circles
                setDrawValues(false) // Remove value labels

                // Enable gradient fill under the line
                setDrawFilled(true)
                fillDrawable = ContextCompat.getDrawable(context, R.drawable.chart_gradient)
            }

            // Assign dataset to the chart
            val lineData = LineData(dataSet)
            this.data = lineData

            // Configure X-axis (date labels)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = android.graphics.Color.WHITE
                textSize = 10f
                granularity = 1f
                labelRotationAngle = -25f // Rotate labels for readability
                valueFormatter = IndexAxisValueFormatter(dates) // Apply date labels
                setDrawGridLines(false) // Hide grid lines
            }

            // Configure Y-axis
            axisLeft.apply {
                setDrawLabels(false) // Hide numeric labels
                setDrawAxisLine(false)
                setDrawGridLines(false) // Hide grid lines
            }
            axisRight.isEnabled = false // Disable right axis

            // Hide chart description and legend
            description.isEnabled = false
            legend.isEnabled = false

            // Set chart background and interaction settings
            setBackgroundColor(android.graphics.Color.parseColor("#0066FF")) // Background color
            setTouchEnabled(true) // Enable touch interaction
            setPinchZoom(false) // Disable zoom

            this.invalidate() // Refresh chart
        }
    }, modifier = Modifier
        .fillMaxWidth()
        .height(250.dp) // Chart height
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, Color.Gray, RoundedCornerShape(16.dp))
    )
}

@Composable
fun TabButton(text: String, isActive: Boolean, onClick: (String) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = { onClick(text) }) // Handle click event
    ) {
        // Display the tab text
        Text(
            text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color.White else Color.LightGray
        )

        Spacer(modifier = Modifier.height(4.dp)) // Space between text and dot indicator

        // Show a green dot indicator under the active tab
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(6.dp) // Small dot size
                    .clip(CircleShape)
                    .background(Color(0xFF00C896)) // Green dot color
            )
        }
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

@RequiresApi(Build.VERSION_CODES.O)
fun getLast30DaysLabels(): List<String> {
    val today = LocalDate.now() // Get current date
    val formatter = DateTimeFormatter.ofPattern("dd MMM") // Define date format

    return (0..30 step 6).map { daysAgo ->
        today.minusDays((30 - daysAgo).toLong()).format(formatter) // Generate past dates
    }
}


