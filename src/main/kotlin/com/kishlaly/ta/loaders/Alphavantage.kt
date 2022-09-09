package com.kishlaly.ta.loaders

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.Timeframe
import com.kishlaly.ta.utils.Quotes
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request

/**
 * For stocks, the last price is always the end of the previous day, regardless of the timeframe
 * There are intraday prices for forex and crypto
 * <p>
 * The timezone is always US/Eastern
 * https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol=IBM&interval=60min&outputsize=full&apikey=
 * https://www.alphavantage.co/query?function=TIME_SERIES_WEEKLY&symbol=TER&&apikey=
 * https://www.alphavantage.co/query?function=EMA&symbol=TER&apikey=&series_type=close&time_period=26&interval=weekly
 *
 * @author Vladimir Kishlaly
 * @since 26.11.2021
 */
class Alphavantage {

    companion object {
        private val gson = Gson()
        private const val KEY = "F4ZNUB0VYAAMTSLP"

        /**
         * Load from alphavantage.co
         *
         * Assuming Exchange Timezone = "US/Eastern"
         */
        fun loadQuotes(symbol: String, timeframe: Timeframe): List<Quote> {
            val quotes = mutableListOf<Quote>()
            val url = getQuotesUrl(symbol, timeframe)
            try {
                getResponse(url)?.let { respone ->
                    var key = when (timeframe) {
                        Timeframe.WEEK -> "Weekly Time Series"
                        Timeframe.DAY -> "Time Series (Daily)"
                        Timeframe.HOUR -> "Time Series (60min)"
                    }
                    val rawQuotes = respone.get(key) as LinkedTreeMap<String, LinkedTreeMap<String, String>>
                    rawQuotes.forEach { k, v ->
                        val day = k
                        val open = v.get("1. open")
                        val high = v.get("2. high")
                        val low = v.get("3. low")
                        val close = v.get("4. close")
                        val volume = v.get("5. volume")

                    }
                } ?: return emptyList()

            } catch (e: Exception) {
                println(e.message)
                return emptyList<Quote>()
            }
            if (quotes.size < Quotes.resolveMinBarsCount(timeframe)) {
                return emptyList<Quote>()
            }
            return quotes.sortedBy { it.timestamp }
        }

        fun getResponse(url: String): Map<String, Object>? {
            return try {
                val httpClient = OkHttpClient()
                val request = Request.Builder().url(url).get().build()
                val body = httpClient.newCall(request).execute().body()
                gson.fromJson<Map<String, Object>>(body.string(),
                        object : TypeToken<Map<String, Object>>() {}.type)
            } catch (e: Exception) {
                null
            }
        }


        fun getQuotesUrl(symbol: String, timeframe: Timeframe): String {
            val prefix = "https://www.alphavantage.co/query?function="
            val url: String
            url = when (timeframe) {
                Timeframe.WEEK -> "${prefix}TIME_SERIES_WEEKLY&symbol=${symbol}&apikey=${KEY}"
                Timeframe.DAY -> "${prefix}TIME_SERIES_DAILY&symbol=${symbol}&outputsize=full&apikey=${KEY}"
                Timeframe.HOUR -> "TIME_SERIES_INTRADAY&interval=60min&symbol=${symbol}&outputsize=full&apikey=${KEY}"
            }
            return url
        }


    }

}