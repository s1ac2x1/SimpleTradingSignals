package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Indicator

data class SymbolData(
    val symbol: String,
    val timeframe: Timeframe,
    var quotes: List<Quote>,
    var indicators: MutableMap<Indicator, List<out AbstractModel>> = mutableMapOf()
) {

    val lastQuote: Quote
        get() {
            return quotes.last()
        }
}