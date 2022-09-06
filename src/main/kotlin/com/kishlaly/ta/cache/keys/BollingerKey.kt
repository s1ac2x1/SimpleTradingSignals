package com.kishlaly.ta.cache.keys

import com.kishlaly.ta.model.Timeframe

data class BollingerKey(override val symbol: String,
                        override val timeframe: Timeframe) : BaseKey(symbol, timeframe) {
}