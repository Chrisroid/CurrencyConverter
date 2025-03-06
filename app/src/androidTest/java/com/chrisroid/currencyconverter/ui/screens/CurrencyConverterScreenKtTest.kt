package com.chrisroid.currencyconverter.ui.screens

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.chrisroid.currencyconverter.viewmodel.CurrencyViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CurrencyConverterScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val mockViewModel: CurrencyViewModel = mockk(relaxed = true)

    @Before
    fun setup() {
        every { mockViewModel.exchangeRate } returns MutableStateFlow(1.2)
        every { mockViewModel.currencySymbols } returns MutableStateFlow(mapOf("USD" to "Dollar", "EUR" to "Euro"))

        composeTestRule.setContent {
            CurrencyConverterScreen(viewModel = mockViewModel)
        }
    }

    @Test
    fun verify_UIElements_AreDisplayed() {
        composeTestRule.onNodeWithText("Currency").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calculator.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Convert").assertIsDisplayed()
    }

    @Test
    fun enterAmount_ShowsCorrectConversion() {
        val amountField = composeTestRule.onNodeWithText("1") // Initial value is "1"
        amountField.performTextInput("10") // Enter "10"
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Exchange Rate: 10 USD = 12.00 EUR").assertExists()
    }

    @Test
    fun clickingConvertButton_TriggersExchangeRateFetch() {
        val convertButton = composeTestRule.onNodeWithText("Convert")
        convertButton.performClick()
        composeTestRule.waitForIdle()

        // Verify UI updates
        composeTestRule.onNodeWithText("Exchange Rate: 1 USD = 1.20 EUR").assertExists()
    }

    @Test
    fun currencyDropdown_CanSelectAnotherCurrency() {
        val dropdown = composeTestRule.onNode(hasText("USD"))
        dropdown.performClick()
        composeTestRule.waitForIdle()

        val option = composeTestRule.onNodeWithText("EUR")
        option.performClick()
        composeTestRule.waitForIdle()

        // Verify selection updated
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
    }
}
