package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Indicator

data class SymbolData(
    val symbol: String,
    val timeframe: Timeframe,
    var quotes: List<Quote>,
    var indicators: MutableMap<Indicator, List<AbstractModel>> = mutableMapOf()
) {

    val lastQuote = quotes.last()

    val preLastQuote = quotes[quotes.size - 2]

    val quotesCount = quotes.size

    val isEmptyQuotes = quotes.isEmpty()

    val isEmptyIndicators = indicators.isEmpty()

    fun clear() {
        indicators.clear()
    }

    fun indicator(indicator: Indicator) = indicators[indicator]

    infix fun quote(index: Int) = quotes[index]

    infix fun lastQuote(index: Int) = quotes[quotesCount - index]
}