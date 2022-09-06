package com.kishlaly.ta.cache.keys

import com.kishlaly.ta.model.Timeframe

data class EMAKey(override val symbol: String,
                  override val timeframe: Timeframe,
                  override val period: Int) : BaseKey(symbol, timeframe, period) {
}