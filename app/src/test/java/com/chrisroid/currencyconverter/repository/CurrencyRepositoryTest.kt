package com.chrisroid.currencyconverter.repository

import com.chrisroid.currencyconverter.model.api.CurrencyApi
import com.chrisroid.currencyconverter.model.dao.CurrencyDao
import com.chrisroid.currencyconverter.model.data.CurrencySymbolEntity
import com.chrisroid.currencyconverter.model.data.CurrencySymbolResponse
import com.chrisroid.currencyconverter.model.data.ExchangeRateResponse
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class CurrencyRepositoryTest {

    private lateinit var repository: CurrencyRepository
    private val mockApi: CurrencyApi = mockk()
    private val mockDao: CurrencyDao = mockk()

    private val testApiKey = "test_api_key"

    @Before
    fun setUp() {
        repository = CurrencyRepository(mockApi, mockDao)
    }

    @Test
    fun `getCurrencySymbols returns local data when available`() = runBlocking {
        // Given: Room database has stored symbols
        val localSymbols = listOf(
            CurrencySymbolEntity("USD", "United States Dollar"),
            CurrencySymbolEntity("EUR", "Euro")
        )
        coEvery { mockDao.getAllSymbols() } returns localSymbols

        // When: Fetching currency symbols
        val result = repository.getCurrencySymbols(testApiKey).first()

        // Then: Result should be the locally stored symbols
        assertTrue(result.isSuccess)
        assertEquals(mapOf("USD" to "United States Dollar", "EUR" to "Euro"), result.getOrNull())
    }

    @Test
    fun `getCurrencySymbols fetches from API when local data is empty`() = runBlocking {
        // Given: Room database is empty, API returns symbols
        coEvery { mockDao.getAllSymbols() } returns emptyList()
        coEvery { mockApi.getSymbols(testApiKey) } returns CurrencySymbolResponse(
            success = true,
            symbols = mapOf("USD" to "United States Dollar", "EUR" to "Euro")
        )
        coEvery { mockDao.insertSymbols(any()) } just Runs

        // When: Fetching currency symbols
        val result = repository.getCurrencySymbols(testApiKey).first()

        // Then: Result should contain API response
        assertTrue(result.isSuccess)
        assertEquals(mapOf("USD" to "United States Dollar", "EUR" to "Euro"), result.getOrNull())
    }

    @Test
    fun `getExchangeRate returns correct exchange rate from API`() = runBlocking {
        // Given: API returns exchange rate
        coEvery {
            mockApi.getLatestExchangeRate(testApiKey, "USD", "EUR")
        } returns ExchangeRateResponse(
            success = true, rates = mapOf("EUR" to 0.85),
            timestamp = 1519296206,
            base = "EUR",
            date = "2025-03-05"
        )

        // When: Fetching exchange rate
        val result = repository.getExchangeRate("USD", "EUR", testApiKey).first()

        // Then: Result should contain exchange rate
        assertTrue(result.isSuccess)
        assertEquals(0.85, result.getOrNull())
    }

    @Test
    fun `getExchangeRate handles API failure`() = runBlocking {
        // Given: API call throws an exception
        coEvery { mockApi.getLatestExchangeRate(any(), any(), any()) } throws IOException("API error")

        // When: Fetching exchange rate
        val result = repository.getExchangeRate("USD", "EUR", testApiKey).first()

        // Then: Ensure it's a failure with correct exception message
        assertTrue(result.isFailure)
        assertEquals("Network error: API error", result.exceptionOrNull()?.message)
    }



}
