package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Indicator

data class SymbolData(
    val symbol: String,
    val timeframe: Timeframe,
    private var quotes: List<Quote>,
    private var indicators: MutableMap<Indicator, List<AbstractModel>> = mutableMapOf()
) {

    val lastQuote = quotes.last()

    val preLastQuote = quotes[quotes.size - 2]

    val quotesCount = quotes.size

    fun clear() {
        indicators.clear()
    }

    fun indicator(indicator: Indicator) = indicators[indicator]

    fun allIndicators() = listOf(indicators)

    fun quote(index: Int) = quotes[index]

    fun allQuotes() = listOf(quotes)

}