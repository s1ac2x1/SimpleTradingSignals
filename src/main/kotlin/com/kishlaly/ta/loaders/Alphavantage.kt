package com.kishlaly.ta.loaders

import com.google.gson.Gson
import com.kishlaly.ta.model.Quote
import com.kishlaly.ta.model.Timeframe

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
        }

        private fun getQuotesUrl(symbol: String, timeframe: Timeframe): String {
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