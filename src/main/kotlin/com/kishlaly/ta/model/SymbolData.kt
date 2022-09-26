package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Indicator

data class SymbolData(
    val symbol: String,
    val timeframe: Timeframe,
    var quotes: List<Quote>,
    var indicators: MutableMap<Indicator, List<AbstractModel>> = mutableMapOf()
) {

    val lastQuote: Quote
        get() {
            return quotes.last()
        }

    val preLastQuote: Quote
        get() {
            return quotes[quotes.size - 2]
        }

    val quotesCount: Int
        get() {
            return quotes.size
        }

    val isEmptyQuotes: Boolean
        get() {
            return quotes.isEmpty()
        }

    val isEmptyIndicators: Boolean
        get() {
            return indicators.isEmpty()
        }

    fun clear() {
        indicators.clear()
    }

    fun indicator(indicator: Indicator) = indicators[indicator]

    infix fun quote(index: Int) = quotes[index]

    infix fun lastQuote(index: Int) = quotes[quotesCount - index]
}