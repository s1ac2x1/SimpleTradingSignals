package com.kishlaly.ta.model

import com.kishlaly.ta.model.indicators.Indicator

data class SymbolData(
        val symbol: String,
        val timeframe: Timeframe,
        val quotes: List<Quote>,
        val indicators: Map<Indicator, List<out AbstractModel>>) {
}